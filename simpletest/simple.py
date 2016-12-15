#!/usr/bin/python

from scripts.helper431Functions import *
import commands, sys, os, time, signal

#for edit distance checking
import Levenshtein


#set up environment
userID = sys.argv[1]
userID.strip("/")
startSequence = 1
commitOffset = 0
ipaddress = "127.0.0.1"
if len(sys.argv) > 2:
    ipaddress = int(sys.argv[2])
os.chdir(userID)

port = 12345
if len(sys.argv) > 3:
    port = int(sys.argv[3])


def testContents(fileName, contents, display=False):
    try:
        print (fileName)
        f = open(fileName,'r')
        print "found file"
    except:
        try:
            print "file not not found in specified dir"
            fileParts = fileName.split("/")
            f = open(fileParts[1])
            print "file found in current path"
        except:
            print "file not found in current path"
            return False
    fileContents = f.read()
    d = Levenshtein.distance(fileContents, contents)
    if d == 0:
        if display:
            print "files contents match"
        return True
    else:
        if display:
            print "files differ by Levenshtein distance", d
            print "expect:"
            print contents
            print "server:"
            print fileContents
            print "-----------"
        return False

def launchServer(port,clean=True):
    if clean:
        print "clean up old server"
        ret = commands.getoutput("sh cleanup.sh")
    print "starting server"
    ret = commands.getoutput("sh run.sh " + ipaddress + " " + str(port) + userID + " > testingLog.log")
    #print "error starting server!!!!!!!!!!!!!!!!!!!!!!!!!!!"

def commonCaseTest(startSequence=1,commitOffset=0):
    print 
    print "****** Common Case Test *********"
    client = Client(port)
    fileName = "common.txt"
    txn = client.new_txn(fileName)
    if txn == -1:
        print "\nFAIL"
        return
    print "Starting transaction with id", txn
    msg1 = "this is a test message"
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    ret = client.commit(txn, 1+commitOffset)
    if ret[0] == "ACK" or ret[0] == "ack":
        print "Recieved ACK"
        print "Testing file contents"
        if testContents(userID+fileName,msg1,True):
            print "\nPASS"
        else:
            print "\nFAIL"
    else:
        print "\nFAIL"
    os.remove(fileName)
        
def writeMultipleMessages(startSequence=1,commitOffset=0):
    print 
    print "****** Multiple Write Test *********"
    client = Client(port)
    fileName = "common1.txt"
    txn = client.new_txn(fileName)
    print "Starting transaction with id", txn
    msg1 = "this is a test message"
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    msg2 = "\nanother test message"
    startSequence += 1
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg2)
    ret = client.commit(txn, 2+commitOffset)
    if ret[0] == "ACK" or ret[0] == "ack":
        print "Recieved ACK"
        print "Testing file contents"
        if testContents(userID+fileName,msg1+msg2,True):
            print "\nPASS"
        else:
            print "\nFAIL"
    else:
        print "\nFAIL"
    os.remove(fileName)
    
    
def omissionFailure(startSequence=1,commitOffset=0):
    print 
    print "****** Omission Failure Test *********"
    client = Client(port)
    fileName = "common2.txt"
    txn = client.new_txn(fileName)
    print "Starting transaction with id", txn
    msg1 = "this is a test message"
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    msg3 = "\nanother test message"
    startSequence += 2
    print "Writing message with seq#", startSequence
    ret = client.writeAck(txn, startSequence, msg3)

    if ret[0] == "ACK_RESEND":
        print "resend received"
    else:
        print "no resend recieved"
    msg2 = "\nthe middle message"
    startSequence -= 1
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg2)
    ret = client.commit(txn, 3+commitOffset)
    if ret[0] == "ACK" or ret[0] == "ack":
        print "Recieved ACK"
        print "Testing file contents"
        if testContents(userID+fileName,msg1+msg2+msg3,True):
            print "\nPASS"
        else:
            print "\nFAIL"
    else:
        print "\nFAIL"
    os.remove(fileName)

def abortedTransactionsTest(startSequence=1,commitOffset=0):
    print
    print "****** abortedTransactions *********"
    client = Client(port)
    fileName = "aborted1.txt"
    txn = client.new_txn(fileName)
    print "Starting transaction with id", txn
    msg1 = "this is a test message"
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    print "Sending Abort Message for txn",txn
    client.abort(txn)
    print "Checking if file was created"
    time.sleep(0.1)
    exists = os.path.isfile(fileName)
    print exists
    print

    fileName = "aborted2.txt"
    print "Creating empty file '", fileName, "' on server"
    tmpFile = open(fileName,'w')
    tmpFile.write('')
    tmpFile.close()
    print "Checking file status"
    print os.path.isfile(fileName)
    
    txn = client.new_txn(fileName)
    print "Starting transaction with id", txn, "for file", fileName
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    print "Sending Abort Message for txn",txn
    client.abort(txn)
    print "Checking if file was modified"
    tmpFile = open(fileName,'r')
    fileContents = tmpFile.read()
    modified = False
    if len(fileContents) > 0:
        modified = True
    print "File modified:", modified
    print
    if exists or modified:
        print "FAIL"
    else:
        print "PASS"
    
    
def readUncommittedFileFailureTest(startSequence=1, commitOffset=0):
    print 
    print "****** Read Uncommitted Test *********"
    client = Client(port)
    fileName = "common.txt"
    txn = client.new_txn(fileName)
    print "Starting transaction with id", txn
    msg1 = "this is a test message"
    print "Writing message with seq#", startSequence
    client.write(txn, startSequence, msg1)
    print "try to read from file"
    read = client.read(fileName)
    if "ERROR" in read:
        print "\nPASS"
    else:
        print "\nFAIL"

def readTest():
    print
    print "****** Reading ******"
    client = Client(port, True)
    fileName = "reading.txt"
    print "Reading "+fileName+" from server"
    print client.read(fileName)

def cleanExit():
    print "****** Stopping Server ******"
    client = Client(port, True)
    client.exitServer()

def basicTests(startSequence=1,commitOffset=0):
    print "testing with startSequence,", startSequence, "and commit offset:", commitOffset
    # commonCaseTest(startSequence, commitOffset)
    # writeMultipleMessages(startSequence, commitOffset)
#     abortedTransactionsTest(startSequence, commitOffset)
#     omissionFailure(startSequence, commitOffset)
#     readTest()
#     readUncommittedFileFailureTest(startSequence, commitOffset)
    cleanExit()
    #omissionFailureTest(startSequence, commitOffset)

# Fork server and Run Tests

cleanupProcesses(port)
child_pid = os.fork()
if child_pid == 0:
    print "Forking server with pid", os.getpid()
    launchServer(port, True)
    sys.exit(0)
else:
    time.sleep(0.5)
    basicTests(startSequence, commitOffset)
    cleanupProcesses(port)
    
