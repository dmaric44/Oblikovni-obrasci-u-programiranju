from DeleteAfterAction import DeleteAfterAction
from DeleteRangeAction import DeleteRangeAction
from InsertTextAction import InsertTextAction
from DeleteBeforeAction import DeleteBeforeAction
from Location import Location
from LocationRange import LocationRange
from UndoManager import UndoManager




class TextEditorModel():
    selectionRange = None

    def __init__(self, lines):
        self.cursorLocation = Location(0,0)
        self.lines = lines.split("\n")
        self.cursorObservers = []
        self.textObservers = []
        self.undoManager = UndoManager()

    def getLines(self):
        return self.lines
    def getLine(self, x):
        return self.lines[x]

    def allLines(self):
        return iter(self.lines)

    def getCursorLocation(self):
        return self.cursorLocation

    def attachCursorObserver(self, obs):
        if(obs not in self.cursorObservers):
            self.cursorObservers.append(obs)
    def dettachCursorObserver(self, obs):
        self.cursorObservers.remove(obs)

    def attachTextObserver(self, obs):
        if(obs not in self.textObservers):
            self.textObservers.append(obs)
    def dettachTextObserver(self, obs):
        self.textObservers.remove(obs)

    def moveCursorLeft(self):
        if(self.cursorLocation.getY()==0):
            if(self.cursorLocation.getX() != 0):
                self.cursorLocation.setLocation(self.cursorLocation.getX()-1, len(self.lines[self.cursorLocation.getX()-1]))
            else:
                return
        else:
            self.cursorLocation.update(0,-1)
        self.notifyCursorObservers()

    def moveCursorRight(self):
        if(self.cursorLocation.getY() == len(self.lines[self.cursorLocation.getX()])):
            if(self.cursorLocation.getX() != len(self.lines)-1):
                self.cursorLocation.setLocation(self.cursorLocation.getX()+1, 0)
            else:
                return
        else:
            self.cursorLocation.update(0,1)
        self.notifyCursorObservers()

    def moveCursorUp(self):
        if(self.cursorLocation.getX() != 0):
            if(self.cursorLocation.getY() > len(self.lines[self.cursorLocation.getX()-1])):
                self.cursorLocation.setLocation(self.cursorLocation.getX()-1, len(self.lines[self.cursorLocation.getX()-1]))
            else:
                self.cursorLocation.update(-1,0)
        self.notifyCursorObservers()

    def moveCursorDown(self):
        if(self.cursorLocation.getX() != len(self.lines)-1):
            if(self.cursorLocation.getY() > len(self.lines[self.cursorLocation.getX()+1])):
                self.cursorLocation.setLocation(self.cursorLocation.getX()+1, len(self.lines[self.cursorLocation.getX()+1]))
            else:
                self.cursorLocation.update(1,0)
        self.notifyCursorObservers()

    def moveCursorToStart(self):
        self.cursorLocation.setLocation(0,0)
        self.notifyCursorObservers()

    def moveCursorToEnd(self):
        x = len(self.lines)-1
        self.cursorLocation.setLocation(x, len(self.lines[x]))
        self.notifyCursorObservers()

    def notifyCursorObservers(self):
        for obs in self.cursorObservers:
            obs.updateCursorLocation(self.cursorLocation)

    def notifyTextObservers(self):
        for obs in self.textObservers:
            obs.updateText()


    def insert(self, text):
        action = InsertTextAction(self, text)
        action.execute_do()
        self.undoManager.push(action)


    def deleteBefore(self):
        action = DeleteBeforeAction(self)
        action.execute_do()
        self.undoManager.push(action)


    def deleteAfter(self):
        action = DeleteAfterAction(self)
        action.execute_do()
        self.undoManager.push(action)

    def deleteRange(self):
        action = DeleteRangeAction(self)
        action.execute_do()
        self.undoManager.push(action)


#     selectionRange
    def hasSelection(self):
        return self.selectionRange != None

    def getSelectionRange(self):
        return self.selectionRange

    def setSelectionRange(self, range):
        self.selectionRange = range

    def getSelectionText(self):
        if(self.selectionRange != None):
            numOfLines = self.selectionRange.getNumOfLinesInRange()
            start = self.selectionRange.getStart()
            end = self.selectionRange.getEnd()

            if(numOfLines == 0):
                return self.lines[start.getX()][start.getY():end.getY()]
            else:
                text = self.lines[start.getX()][start.getY():] + "\n"

                i = start.getX()+1
                while(i<end.getX()):
                    text += self.lines[i] + "\n"
                    i += 1
                text += self.lines[end.getX()][0:end.getY()]
                return text
        else:
            return ""



    def addSelectionRight(self):
        if(self.cursorLocation.getY() == len(self.lines[self.cursorLocation.getX()])):
            if(self.cursorLocation.getX() < len(self.lines)-1):
                x = self.cursorLocation.getX()+1
                while(len(self.lines[x]) == 0):
                    x += 1
                    if(x > len(self.lines)):
                        return
                if(self.selectionRange == None):
                    self.selectionRange = LocationRange(None, None)
                    self.selectionRange.setStart(Location(x,0))
                    self.selectionRange.setEnd(Location(x,1))
                else:
                    if(self.cursorLocation.__eq__(self.selectionRange.getStart())):
                        self.selectionRange.setStart(Location(x,1))
                    else:
                        self.selectionRange.setEnd((Location(x,1)))
                self.cursorLocation.setLocation(x,1)
            else:
                return
        else:
            if(self.selectionRange == None):
                self.selectionRange = LocationRange(Location(self.cursorLocation.getX(),self.cursorLocation.getY()), Location(self.cursorLocation.getX(),self.cursorLocation.getY()+1))
            else:
                if(self.cursorLocation.__eq__(self.selectionRange.getEnd())):
                    self.selectionRange.updateEnd(0,1)
                else:
                    self.selectionRange.updateStart(0,1)
            self.moveCursorRight()
        self.notifyTextObservers()

    def addSelectionLeft(self):
        if(self.cursorLocation.getY() == 0):
            if(self.cursorLocation.getX() != 0):
                x = self.cursorLocation.getX()-1
                while(len(self.lines[x])==0):
                    x -= 1
                    if(x<0):
                        return
                if(self.selectionRange == None):
                    self.selectionRange = LocationRange(None, None)
                    self.selectionRange.setStart(Location(x, len(self.lines[x])-1))
                    self.selectionRange.setEnd(Location(x, len(self.lines[x])))
                else:
                    if(self.cursorLocation.__eq__(self.selectionRange.getStart())):
                        start = Location(x, len(self.lines[x])-1)
                        self.selectionRange.setStart(start)
                    else:
                        end = Location(x, len(self.lines[x])-1)
                        self.selectionRange.setEnd(end)
                self.cursorLocation.setLocation(x, len(self.lines[x])-1)
            else:
                return

        else:
            if(self.selectionRange == None):
                self.selectionRange = LocationRange(Location(self.cursorLocation.getX(),self.cursorLocation.getY()-1), Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
            else:
                if(self.cursorLocation.__eq__(self.selectionRange.getStart())):
                    self.selectionRange.updateStart(0,-1)
                else:
                    self.selectionRange.updateEnd(0,-1)
            self.moveCursorLeft()
        self.notifyTextObservers()

    def addSelectionUp(self):
        if(self.cursorLocation.getX() == 0):
            return
        if(self.hasSelection()):
            if(self.cursorLocation.__eq__(self.selectionRange.getStart())):
                lineLen = len(self.lines[self.cursorLocation.getX()-1])
                if(self.cursorLocation.getY() > lineLen):
                    self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, lineLen))
                    self.cursorLocation.setLocation(self.cursorLocation.getX()-1, lineLen)
                else:
                    self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, self.cursorLocation.getY()))
                    self.cursorLocation.update(-1,0)
            elif(self.cursorLocation.__eq__(self.selectionRange.getEnd())):
                if(self.selectionRange.getStart().getX() == self.cursorLocation.getX()):
                    self.selectionRange.setEnd(self.selectionRange.getStart())
                    lineLen = len(self.lines[self.cursorLocation.getX()-1])
                    if(self.cursorLocation.getY() > lineLen):
                        self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, lineLen))
                        self.cursorLocation.setLocation(self.cursorLocation.getX()-1, lineLen)
                    else:
                        self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, self.cursorLocation.getY()))
                        self.cursorLocation.setLocation(self.cursorLocation.getX()-1, self.cursorLocation.getY())
                else:
                    if(self.selectionRange.getStart().getY() > self.cursorLocation.getY() and self.selectionRange.getStart().getX() == self.cursorLocation.getX()-1):
                        self.selectionRange.setEnd(self.selectionRange.getStart())
                        self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, self.cursorLocation.getY()))
                        self.cursorLocation.setLocation(self.cursorLocation.getX()-1, self.cursorLocation.getY())
                    else:
                        lineLen = len(self.lines[self.cursorLocation.getX()-1])
                        if(self.cursorLocation.getY() > lineLen):
                            self.cursorLocation.setLocation(self.cursorLocation.getX()-1, lineLen)
                        else:
                            self.cursorLocation.update(-1,0)
                        self.selectionRange.setEnd(Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
        else:
            self.selectionRange = LocationRange(None, None)
            self.selectionRange.setEnd(Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
            lineLen = len(self.lines[self.cursorLocation.getX()-1])
            if(self.cursorLocation.getY() > lineLen):
                self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, lineLen))
                self.cursorLocation.setLocation(self.cursorLocation.getX()-1, lineLen)
            else:
                self.selectionRange.setStart(Location(self.cursorLocation.getX()-1, self.cursorLocation.getY()))
                self.cursorLocation.update(-1,0)
        self.notifyTextObservers()

    def addSelectionDown(self):
        if(self.cursorLocation.getX() == len(self.lines)-1):
            return

        if(self.hasSelection()):
            if(self.cursorLocation.__eq__(self.selectionRange.getEnd())):
                lineLen = len(self.lines[self.cursorLocation.getX()+1])
                if(self.cursorLocation.getY() > lineLen):
                    self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, lineLen))
                    self.cursorLocation.setLocation(self.cursorLocation.getX()+1, lineLen)
                else:
                    self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, self.cursorLocation.getY()))
                    self.cursorLocation.update(1,0)
            elif(self.cursorLocation.__eq__(self.selectionRange.getStart())):
                if(self.selectionRange.getStart().getX() == self.cursorLocation.getX()):
                    self.selectionRange.setStart(self.selectionRange.getEnd())
                    lineLen = len(self.lines[self.cursorLocation.getX()+1])
                    if(self.cursorLocation.getY() > lineLen):
                        self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, lineLen))
                        self.cursorLocation.setLocation(self.cursorLocation.getX()+1, lineLen)
                    else:
                        self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, self.cursorLocation.getY()))
                        self.cursorLocation.update(1,0)
                else:
                    if(self.selectionRange.getEnd().getY() < self.cursorLocation.getY() and self.selectionRange.getEnd().getX() == self.cursorLocation.getX()+1):
                        self.selectionRange.setStart(self.selectionRange.getEnd())
                        lineLen = len(self.allLines[self.cursorLocation.getX()+1])
                        if(self.cursorLocation.getY() > lineLen):
                            self.cursorLocation.setLocation(self.cursorLocation.getX()+1, lineLen)
                        else:
                            self.cursorLocation.update(1,0)
                        self.selectionRange.setEnd(Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
                    else:
                        lineLen = len(self.lines[self.cursorLocation.getX()+1])
                        if(self.cursorLocation.getY() > lineLen):
                            self.cursorLocation.setLocation(self.cursorLocation.getX()+1, lineLen)
                        else:
                            self.cursorLocation.update(1,0)
                        self.selectionRange.setStart(Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
        else:
            self.selectionRange = LocationRange(None, None)
            self.selectionRange.setStart(Location(self.cursorLocation.getX(), self.cursorLocation.getY()))
            lineLen = len(self.lines[self.cursorLocation.getX()+1])
            if(self.cursorLocation.getY() > lineLen):
                self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, lineLen))
                self.cursorLocation.setLocation(self.cursorLocation.getX()+1, lineLen)
            else:
                self.selectionRange.setEnd(Location(self.cursorLocation.getX()+1, self.cursorLocation.getY()))
                self.cursorLocation.update(1,0)
        self.notifyTextObservers()








