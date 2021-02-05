module prtwod

#time "on"
#r "nuget: Akka.FSharp" 
#r "nuget: Akka.TestKit"

open System
open Akka.FSharp
open Akka.Actor

let system = System.create "MySystem" <| Configuration.load()
let mutable numnodes = 0.0
let mutable spread = 0
let mutable flag = false

let mutable root = ceil (sqrt (float numnodes))
let mutable nodes = Array.create ((int)(root*root)+1) 0
let mutable rumorarr = Array.create ((int)(root*root)+1) ""
let mutable timearr = Array.create ((int)(root*root)+1) 0



let mutable m = Map.empty
let mutable l = [0.0]
let mutable nodem:Map<float,List<float>> = Map.empty

type ProcessorJob = Msg of string * int * int * int

let workerref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()
            let shuffleR (r : Random) xs = xs |> Seq.sortBy (fun _ -> r.Next())
            let mutable r = m.[(float)id] |> shuffleR (Random ()) |> Seq.head;
            while r = (float)id do
                r <- m.[(float)id] |> shuffleR (Random ()) |> Seq.head;
            if rumorarr.[(int)r] = "" then
                rumorarr.[(int)r] <- "RN"
            let ratio1 = abs ((nodem.[r].Item(4)/nodem.[r].Item(5)) - (nodem.[r].Item(2)/nodem.[r].Item(3)))
            let ratio2 = abs ((nodem.[r].Item(2)/nodem.[r].Item(3)) - (nodem.[r].Item(0)/nodem.[r].Item(1)))
            if ratio1 > 0.0000000001 && ratio2 > 0.0000000001 then
                timearr.[(int)r] <- time
                nodem <- nodem.Add(r,[nodem.[r].Item(2);nodem.[r].Item(3);nodem.[r].Item(4);nodem.[r].Item(5);nodem.[r].Item(4)+nodem.[float id].Item(4);nodem.[r].Item(5)+nodem.[float id].Item(5)])
            return! loop() }
        loop()

let bossref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()
            let l = System.Random()
            let r = l.Next(1,(int)numnodes)
            rumorarr.[r] <- "RN"
            let mutable time = 1
            while ((float) spread/(float) numnodes < 0.6) do
                for i = 1 to (int)numnodes do
                    let childi = l.Next(1,10000000)
                    let r1 = abs ((nodem.[float i].Item(4)/nodem.[float i].Item(5)) - (nodem.[float i].Item(2)/nodem.[float i].Item(3))) // add 0 divide error
                    let r2 = abs ((nodem.[float i].Item(2)/nodem.[float i].Item(3)) - (nodem.[float i].Item(0)/nodem.[float i].Item(1)))
                    if rumorarr.[i] <> "" && time <> timearr.[i] && r1 > 0.0000000001 && r2 > 0.0000000001 then
                        nodem <- nodem.Add(float i,[nodem.[float i].Item(0);nodem.[float i].Item(1);nodem.[float i].Item(2);nodem.[float i].Item(3);nodem.[float i].Item(4)/2.0;nodem.[float i].Item(5)/2.0])
                        let child = spawn system (sprintf "child%i" i+ (string)childi + (string)time) workerref // recheck later
                        child <! Msg(rumor,i,nodes.[i],time)
                    elif r1 <= 0.0000000001 || r2 <= 0.0000000001 then
                        spread <- spread + 1
                time <- time + 1
            return! loop()
        }   
        loop()

let start (numNodes: int) (topology: string) = 
    let stopWatch = System.Diagnostics.Stopwatch.StartNew()
    numnodes <- (float) numNodes

    root <- ceil (sqrt (float numnodes))
    nodes <- Array.create ((int)(root*root)+1) 0
    rumorarr <- Array.create ((int)(root*root)+1) ""
    timearr <- Array.create ((int)(root*root)+1) 0

    numnodes <- root*root

    for i in 1.0 .. numnodes do
        nodem <- nodem.Add(i,[(float) i;1.0;0.0;1.0;(float) i;1.0])

    let obj = System.Random()
    for i in 1.0 .. root .. numnodes do
        for j in i .. root + i  - 1.0 do
            if j = 1.0 then //Top left corner
                l <- [j+1.0;j+root] 
            elif j = root then //Top right corner
                l <- [j-1.0;j+root]
            elif j = numnodes then //Bottom right corner
                l <- [j-1.0;j-root]
            elif j = numnodes - root + 1.0 then //Bottom left corner
                l <- [j+1.0;j-root]    
            elif j <> 1.0 && j = i then //Left boundary
                l <- [j+1.0;j-root;j+root]
            elif j <> 1.0 && j < root then //Top boundary
                l <- [j - 1.0;j+1.0;j+root]
            elif j <> root && j = (root + i - 1.0) then //Right boundary
                l <- [j - 1.0;j-root;j+root]
            elif j <> numnodes && j > (numnodes - root + 1.0) then //Bottom boundary
                l <- [j - 1.0;j+1.0;j-root]
            else 
                l <- [j - 1.0;j + 1.0;j-root;j+root] //Center
            let mutable r = float (obj.Next(1,int (numnodes)))
            while List.contains r l do
                r <- float (obj.Next(1,int (numnodes)))
            l <- r :: l
            m <- m.Add(j,l)

    let boss = spawn system "Boss" bossref
    boss <! Msg("",0,0,0)

    while not flag do
        if (float) spread/(float) numnodes >= 0.6 then
            flag <- true
            
    stopWatch.Stop()
    printfn "Total time is %f milliseconds" stopWatch.Elapsed.TotalMilliseconds