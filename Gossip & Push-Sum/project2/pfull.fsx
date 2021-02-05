module pfull

#time "on"
#r "nuget: Akka.FSharp" 
#r "nuget: Akka.TestKit"

open System
open Akka.FSharp
open Akka.Actor

let system = System.create "MySystem" <| Configuration.load()

let mutable numnodes = 0
let mutable spread = 0
let mutable flag = false
let mutable nodes = Array.create (numnodes+1) 0
let mutable rumorarr = Array.create (numnodes+1) ""
let mutable timearr = Array.create (numnodes+1) 0
type ProcessorJob = Msg of string * int * int * int
let mutable nodem:Map<int,List<float>> = Map.empty

let mutable m = Map.empty

let workerref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()

            let shuffleR (r : Random) xs = xs |> Seq.sortBy (fun _ -> r.Next())
            let mutable r = m.[id] |> shuffleR (Random ()) |> Seq.head;
            
            if rumorarr.[r] = "" then
                rumorarr.[r] <- "RN"
            let ratio1 = abs((nodem.[r].Item(4)/nodem.[r].Item(5)) - (nodem.[r].Item(2)/nodem.[r].Item(3)))
            let ratio2 = abs((nodem.[r].Item(2)/nodem.[r].Item(3)) - (nodem.[r].Item(0)/nodem.[r].Item(1)))  
            if ratio1 > 0.0000000001 && ratio2 > 0.0000000001 then
                timearr.[r] <- time
                nodem <- nodem.Add(r,[nodem.[r].Item(2);nodem.[r].Item(3);nodem.[r].Item(4);nodem.[r].Item(5);nodem.[r].Item(4)+nodem.[id].Item(4);nodem.[r].Item(5)+nodem.[id].Item(5)])
            return! loop() }
        loop()

let bossref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()
            let l = System.Random()
            let mutable r = l.Next(1,numnodes)
            rumorarr.[r] <- "RN"
            let mutable time = 1
            while ((float) spread/(float) numnodes < 0.6) do
                for i = 1 to numnodes do
                    let childi = l.Next(1,100000000)
                    let r1 = abs ((nodem.[i].Item(4)/nodem.[i].Item(5)) - (nodem.[i].Item(2)/nodem.[i].Item(3)))
                    let r2 = abs((nodem.[i].Item(2)/nodem.[i].Item(3)) - (nodem.[i].Item(0)/nodem.[i].Item(1)))
                    if rumorarr.[i] <> "" && time <> timearr.[i] && r1 > 0.0000000001 && r2 > 0.0000000001 then
                        nodem <- nodem.Add(i,[nodem.[i].Item(0);nodem.[i].Item(1);nodem.[i].Item(2);nodem.[i].Item(3);nodem.[i].Item(4)/2.0;nodem.[i].Item(5)/2.0])
                        let child = spawn system (sprintf "child%i" i + (string)childi + (string)time) workerref
                        child <! Msg(rumor,i,nodes.[i],time)
                    elif r1 <= 0.0000000001 || r2 <= 0.0000000001 then
                        spread <- spread + 1
                time <- time + 1
            return! loop()
        }   
        loop()

let start (numNodes: int) (topology: string) = 
    let stopWatch = System.Diagnostics.Stopwatch.StartNew()
    numnodes <- numNodes
    nodes <- Array.create (numnodes+1) 0
    rumorarr <- Array.create (numnodes+1) ""
    timearr <- Array.create (numnodes+1) 0

    for i = 1 to numnodes do
        m <- m.Add(i,[for a in 1 .. numnodes do if a <> i then yield a]);

    for i = 1 to numnodes do
        nodem <- nodem.Add(i,[(float) i;1.0;0.0;1.0;(float) i;1.0])

    let boss = spawn system "Boss" bossref
    boss <! Msg("",0,0,0)

    while not flag do
        if (float) spread/(float) numnodes >= 0.6 then
            flag <- true

    stopWatch.Stop()
    printfn "Total time is %f milliseconds" stopWatch.Elapsed.TotalMilliseconds