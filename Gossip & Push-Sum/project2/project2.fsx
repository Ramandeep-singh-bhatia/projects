#load "full.fsx"
#load "pfull.fsx"
#load "line.fsx"
#load "pline.fsx"
#load "twod.fsx"
#load "ptwod.fsx"
#load "rtwod.fsx"
#load "prtwod.fsx"

let project2() = 
    let numNodes = int fsi.CommandLineArgs.[1]
    let topology = string fsi.CommandLineArgs.[2]
    let algorithm = string fsi.CommandLineArgs.[3]

    if algorithm = "gossip" then
        if topology = "full" then
            full.start numNodes topology
        if topology = "line" then
            line.start numNodes topology
        if topology = "2D" then
            twod.start numNodes topology
        if topology = "imp2D" then
            rtwod.start numNodes topology
    elif algorithm = "push-sum" then
        if topology = "full" then
            pfull.start numNodes topology
        if topology = "line" then
            pline.start numNodes topology
        if topology = "2D" then
            ptwod.start numNodes topology
        if topology = "imp2D" then
            prtwod.start numNodes topology
    else
        printfn "Incorrect input parameters"
project2()