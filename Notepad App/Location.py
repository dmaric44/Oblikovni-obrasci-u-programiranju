class Point():
    def __init__(self, x,y):
        self.x = x
        self.y = y



class Location:
    def __init__(self, x, y):
        self.location = Point(x,y)


    def getX(self):
        return self.location.x
    def getY(self):
        return self.location.y

    def setLocation(self, x, y):
        self.location.x = x
        self.location.y = y

    def getLocation(self):
        return self.location

    def update(self, x,y):
        self.location.x += x
        self.location.y +=y

    def __eq__(self, loc):
        return isinstance(loc, Location) and loc.getX() == self.getX() and loc.getY() == self.getY()

        