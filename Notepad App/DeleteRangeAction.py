from EditAction import EditAction
from Location import Location
from LocationRange import LocationRange


class DeleteRangeAction(EditAction):

    def __init__(self, model):
        self.model = model
        self.previousStateOfLines = None
        self.previousCursorLocation = None
        self.selectionRange = LocationRange(self.model.selectionRange.getStart(), self.model.selectionRange.getEnd())


    def execute_do(self):
        self.previousStateOfLines = self.model.lines.copy()
        self.previousCursorLocation = Location(self.model.getCursorLocation().getX(),self.model.getCursorLocation().getY())

        if (self.model.cursorLocation.__eq__(self.selectionRange.getEnd())):
            self.model.cursorLocation.setLocation(self.selectionRange.getStart().getX(),self.selectionRange.getStart().getY())
        if (self.selectionRange.getNumOfLinesInRange() == 0):
            line = self.model.lines[self.selectionRange.getStart().getX()]
            line = line[:self.selectionRange.getStart().getY()] + line[self.selectionRange.getEnd().getY():]
            self.model.lines[self.selectionRange.getStart().getX()] = line
        else:
            i = self.selectionRange.getStart().getX() + 1
            while (i < self.selectionRange.getStart().getX() + self.selectionRange.getNumOfLinesInRange()):
                self.model.lines.pop(i)
                i += 1

            self.model.lines[self.selectionRange.getStart().getX()] = self.model.lines[self.selectionRange.getStart().getX()][:self.selectionRange.getStart().getY()] + self.model.lines[self.selectionRange.getStart().getX() + 1][ self.selectionRange.getEnd().getY():]
            self.model.lines.pop(self.selectionRange.getStart().getX() + 1)
        self.model.selectionRange = None
        self.model.notifyTextObservers()

    def execute_undo(self):
        self.model.lines = self.previousStateOfLines.copy()
        self.model.cursorLocation.setLocation(self.previousCursorLocation.getX(), self.previousCursorLocation.getY())
        self.model.notifyTextObservers()