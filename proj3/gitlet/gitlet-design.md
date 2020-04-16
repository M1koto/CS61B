# Gitlet Design Document

**Kenny Liao**

#Classes and Data Structures
###Main
This class intakes commands and sets up proper files and directories.

    1.Commands (init, add, commit, rm, log, global-log, find, status, checkout, branch, rm-branch, reset, merge)
    2. Dir (.gitlet [staging area, ])
    3. Files
    4. Failure handling
    5. EC
    
###User Class (permanent):
This class stores permanent information for 'user' and supports finding, managing, reading all hash.

Fields: Latest Commit info, so that can just return from user class
###Important: Takes care of HEAD (pointer), and branch head (pointer) to DoubleHT


Also: Another data structure for storing message for find

Includes: All History

###Double HT Class:
This class implements linked list by allowing its pointer to point to at most two double HT objects (branch), 
and allows it to be pointed by at most two double HT object (merge).

Constructor: DoubleHT(Parent p, Commit c)

Fields: Parent1, Parent2, Child1, Child2, branch1, branch2

At least will have Parent1 & branch1 != null except first commit

###Commit class: 
This class calls compare class on all file and get difference between latest commit and version now?

Tracks all files in staging class, receives info from user class for latest commit info, synthesis data adding on metadata to produce a new commit
In case of merge calls user class twice, merge, and create new commit.

Fields: log message, time stamp, Data Structure for all files tracked

Important: "Parent Commit": For maintaining branch structure, merge, 

###Staging Class (permanent):
This class provides an interface between adding and committing where edits can be done.

Fields: Data structure for all files for addition, address?
###Compare class : 
This class compares individual file with its latest commits, noticing addition and deletion of changes.

# Algorithms
####Commit(log, time, data): creates a commit instance, pointing to its parent
####Compare(): Method in compare class that compares two files and returns differences
####track(File f): Tracks File f in the commit class to track File F for commits
####Untrack(File f): Untracks File f in commit class by the rm command

####Double HT:
addChild(), addParent()

add(DoubleHT c, branch b): adds DoubleHT c to branch b and makes c branch head. adding b to branch field. errors of > 2 branch

find(Message M): Returns all commit id associated with the log Message m.

find(Commit C): Returns all the information about the commit C (done by searching files through directory path). Including time stamp, message, and data

branch(String s) in user class: Creates a new branch with the given name, and points it at the current head node.
# Persistence
For each and every single commit, create a dir containing commit name inside .gitlet. The dir contains all files that the commit tracks.
The only data needed to 'persist' is the current staging class and all the commits made since gitlet init?

Info needed to be remembered:
1. Staging class: As individual adds or deletes will affect history actions

2. User class: Contains entire branch, hence also the latest commit info

#Overall Structure:
Main -> User class (remembers HEAD and branch heads) -> DoubleHT in User class -> commit class -> staging class -> compare class

#End
