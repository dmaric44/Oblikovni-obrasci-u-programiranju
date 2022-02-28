

class LocationRange():
    start = None
    end = None

    def __init__(self, start, end):
        self.start = start
        self.end = end

    def getNumOfLinesInRange(self):
        return self.end.getX() - self.start.getX()

    def updateStart(self, x,y):
        self.start.update(x,y)
    def updateEnd(self, x,y):
        self.end.update(x,y)

    def setStart(self, start):
        self.start = start
    def setEnd(self, end):
        self.end = end

    def getStart(self):
        return self.start
    def getEnd(self):
        return self.end




