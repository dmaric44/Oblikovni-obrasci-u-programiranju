from EditAction import EditAction
from Location import Location


class DeleteBeforeAction(EditAction):

    def __init__(self, model):
        self.model = model
        self.previousStateOfLines = None
        self.previousCursorLocation = None

    def execute_do(self):
        self.previousStateOfLines = self.model.lines.copy()
        self.previousCursorLocation = Location(self.model.getCursorLocation().getX(), self.model.getCursorLocation().getY())

        if (self.model.cursorLocation.getY() == 0):
            if (self.model.cursorLocation.getX() != 0):
                currLine = self.model.lines[self.model.cursorLocation.getX()]
                prevLineLen = len(self.model.lines[self.model.cursorLocation.getX() - 1])
                self.model.lines[self.model.cursorLocation.getX() - 1] = self.model.lines[self.model.cursorLocation.getX() - 1] + currLine
                self.model.lines.pop(self.model.cursorLocation.getX())
                self.model.cursorLocation.update(-1, prevLineLen)
            else:
                return
        else:
            currLine = self.model.lines[self.model.cursorLocation.getX()]
            self.model.lines[self.model.cursorLocation.getX()] = currLine[:self.model.cursorLocation.getY() - 1] + currLine[ self.model.cursorLocation.getY():]
            self.model.cursorLocation.update(0, -1)
        self.model.notifyTextObservers()

    def execute_undo(self):
        self.model.lines = self.previousStateOfLines.copy()
        self.model.cursorLocation.setLocation(self.previousCursorLocation.getX(), self.previousCursorLocation.getY())
        self.model.notifyTextObservers()