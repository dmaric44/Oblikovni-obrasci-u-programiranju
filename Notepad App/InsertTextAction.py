from EditAction import EditAction
from Location import Location
from LocationRange import LocationRange


class InsertTextAction(EditAction):
    model = None

    def __init__(self, model, text):
        self.model = model
        self.text = text

        self.previousCursorLocation = None
        self.previousStateOfLines = None
        self.previousSelectionRange = None


    def execute_do(self):
        # line i cursLoc izvaditi adko ne bude radilo
        self.previousStateOfLines = self.model.lines.copy()
        self.previousCursorLocation = Location(self.model.getCursorLocation().getX(), self.model.getCursorLocation().getY())
        self.previousSelectionRange = self.model.getSelectionRange()

        if(self.model.hasSelection()):
            self.model.deleteRange()

        if (isinstance(self.text, int)):
            if(self.text == 10):
                line = self.model.lines[self.model.cursorLocation.getX()]
                firstPart = line[:self.model.cursorLocation.getY()]
                secondPart = line[self.model.cursorLocation.getY():]
                self.model.lines[self.model.cursorLocation.getX()] = firstPart
                self.model.lines.insert(self.model.cursorLocation.getX() + 1, secondPart)
                self.model.cursorLocation.setLocation(self.model.cursorLocation.getX() + 1, 0)
            elif(self.text == 32):
                line = self.model.lines[self.model.cursorLocation.getX()]
                firstPart = line[:self.model.cursorLocation.getY()]
                secondPart = line[self.model.cursorLocation.getY():]
                self.model.lines[self.model.cursorLocation.getX()] = firstPart + " " + secondPart
                self.model.moveCursorRight()

        else:
            if(self.text is not None):
                if (len(self.text) == 1):
                    line = self.model.lines[self.model.cursorLocation.getX()]
                    line = line[:self.model.cursorLocation.getY()] + self.text + line[self.model.cursorLocation.getY():]
                    self.model.lines[self.model.cursorLocation.getX()] = line
                    self.model.cursorLocation.update(0, 1)
                else:
                    newLines = self.text.split("\n")
                    line = self.model.lines[self.model.cursorLocation.getX()]
                    firstPart = line[:self.model.cursorLocation.getY()]
                    secPart = line[self.model.cursorLocation.getY():]
                    if (len(newLines) == 1):
                        self.model.lines[self.model.cursorLocation.getX()] = firstPart + newLines[0] + secPart
                        self.model.cursorLocation.update(0, len(newLines[0]))
                    else:
                        self.model.lines[self.model.cursorLocation.getX()] = firstPart + newLines[0]
                        i = self.model.cursorLocation.getX() + 1
                        for j in range(1, len(newLines) - 1):
                            self.model.lines.insert(i, newLines[j])
                            i += 1
                        lastLine = newLines.pop()
                        self.model.lines.insert(i, lastLine + secPart)
                        self.model.cursorLocation.setLocation(i, len(lastLine))
        self.model.notifyTextObservers()

    def execute_undo(self):
        # print(self.model.lines)
        # print(self.previousStateOfLines)
        # print(self.previousCursorLocation.getX(), self.previousCursorLocation.getY())
        # print(self.model.cursorLocation.getX(), self.model.cursorLocation.getY())
        self.model.lines = self.previousStateOfLines
        self.model.cursorLocation.setLocation(self.previousCursorLocation.getX(), self.previousCursorLocation.getY())
        self.model.selectionRange = self.previousSelectionRange
        self.model.notifyTextObservers()