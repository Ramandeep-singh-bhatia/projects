module line

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

let mutable m = Map.empty

let workerref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()

            let shuffleR (r : Random) xs = xs |> Seq.sortBy (fun _ -> r.Next())
            let mutable r = m.[id] |> shuffleR (Random ()) |> Seq.head;

            if rumorarr.[r] = "" then
                rumorarr.[r] <- "RN"
                spread <- spread + 1  
            if nodes.[r] < 10 then
                nodes.[r] <- nodes.[r]+1
                timearr.[r] <- time
            return! loop() }
        loop()


let bossref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()
            let l = System.Random()
            let mutable r = l.Next(1,numnodes)
            nodes.[r] <- nodes.[r]+1
            rumorarr.[r] <- "RN"
            spread <- spread+1

            let mutable time = 1
            while ((float) spread/(float) numnodes < 0.6) do
                for i = 1 to numnodes do
                    let childi = l.Next(1,100000000)
                    if rumorarr.[i] <> "" && nodes.[i] < 10 && time <> timearr.[i] then
                        let child = spawn system (sprintf "child%i" i + (string)childi + (string)time) workerref // recheck later
                        child <! Msg(rumor,i,nodes.[i],time)
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
    
    let l1 = [2]
    m <- m.Add(1,l1)
    for i = 2 to (numnodes-1) do
        let list2 = [i-1;i+1]
        m <- m.Add(i,list2)
    let l2 = [numnodes-1]
    m <- m.Add(numnodes,l2)

    let boss = spawn system "Boss" bossref
    boss <! Msg("",0,0,0)

    while not flag do
        if (float) spread/(float) numnodes >= 0.6 then
            flag <- true
    
    stopWatch.Stop()
    printfn "Total time is %f milliseconds" stopWatch.Elapsed.TotalMilliseconds