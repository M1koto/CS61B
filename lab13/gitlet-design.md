# Gitlet Design Document

**Kenny Liao**

#Classes and Data Structures
###Main
This class intakes commands and sets up proper files and directories.

    1.Commands (init, add, commit, rm, log, global-log, find, status, checkout, branch, rm-branch, reset, merge)
    2. Dir (.gitlet [staging area, ])
    3. Files
    4. Failure handling
    
###User Class (permanent):
This class stores permanent information for 'user' and supports finding, managing, reading all hash.

Important: Branches, 

Includes: log History, Working Directory
    
###Data Class:
This class stores all pointers to necessary info for commit.

###Commit class: 
This class calls compare class on all file and get difference between latest commit and version now?


Fields: log message, time stamp, calls on "data class"

###Staging Class (permanent):
This class provides an interface between adding and committing where edits can be done.

###Compare class : 
This class compares individual file with its latest commits, noticing addition and deletion of changes.


# Algorithms
####Commit(log, time, data): creates a commit instance, pointing to its parent
####Compare(): Method in compare class that compares two files and returns differences
####track(File f): Creates a pointer in the commit class to track File F for commits
####Untrack(): Find untrack files (idk which class to put it in yet)

# Persistence
For each and every single commit, create a dir containing commit name inside .gitlet. The dir contains all files that the commit tracks.
The only data needed to 'persist' is the current working directory and all the commits made since gitlet init?

