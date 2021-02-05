module rtwod

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
                spread <- spread + 1  
            if nodes.[(int)r] < 10 then
                nodes.[(int)r] <- nodes.[(int)r]+1
                timearr.[(int)r] <- time
            return! loop() }
        loop()


let bossref (mailbox : Actor<_>) =
        let rec loop() = actor {
            let! Msg(rumor,id,count,time) = mailbox.Receive()
            let l = System.Random()
            let c = System.Random()
            let mutable r = l.Next(1,(int)numnodes)
            nodes.[r] <- nodes.[r]+1
            rumorarr.[r] <- "RN"
            spread <- spread+1
            
            let mutable time = 1
            while ((float) spread/(float) numnodes < 0.7) do
                for i = 1 to (int)numnodes do
                    let childi = l.Next(1,100000000)
                    if rumorarr.[i] <> "" && nodes.[i] < 10 && time <> timearr.[i] then
                        let child = spawn system (sprintf "child%i" childi + (string)i + (string)time) workerref // recheck later
                        child <! Msg(rumor,i,nodes.[i],time)
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

    let obj = System.Random()
    numnodes <- root*root
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
        if (float) spread/(float) numnodes >= 0.7 then
            flag <- true

    stopWatch.Stop()
    printfn "Total time is %f milliseconds" stopWatch.Elapsed.TotalMilliseconds