class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]


class UndoManager(metaclass = Singleton):

    def __init__(self):
        self.undoStack = []
        self.redoStack = []

        self.undoStackObservers = []
        self.redoStackObservers = []


    def undo(self):
        if(len(self.undoStack) != 0):
            action = self.undoStack.pop()
            # print(action.text)
            action.execute_undo()

            if(len(self.undoStack) == 0):
                self.notifyUndoStackObservers("EMPTY")

            self.redoStack.append(action)
            self.notifyRedoStackObservers("HAS_ELEMENTS")
        else:
            self.notifyUndoStackObservers("EMPTY")

    def redo(self):
        if(len(self.redoStack) != 0):
            action = self.redoStack.pop()
            action.execute_do()

            if(len(self.redoStack) == 0):
                self.notifyRedoStackObservers("EMPTY")

            self.undoStack.append(action)
            self.notifyUndoStackObservers("HAS_ELEMENTS")
        else:
            self.notifyRedoStackObservers("EMPTY")

    def push(self, action):
        self.redoStack.clear()
        self.undoStack.append(action)
        self.notifyUndoStackObservers("HAS_ELEMENTS")
        self.notifyRedoStackObservers("EMPTY")



    def addUndoStackObservers(self, obs):
        if(obs not in self.undoStackObservers):
            self.undoStackObservers.append(obs)

    def removeUndoObservers(self, obs):
        self.undoStackObservers.remove(obs)

    def notifyUndoStackObservers(self, state):
        if(state == 'EMPTY'):
            for obs in self.undoStackObservers:
                obs.stackEmpty()
        else:
            for obs in self.undoStackObservers:
                obs.stackHasElements()




    def addRedoStackObservers(self, obs):
        if(obs not in self.redoStackObservers):
            self.redoStackObservers.append(obs)

    def removeUndoObservers(self, obs):
        self.redoStackObservers(obs)

    def notifyRedoStackObservers(self, state):
        if(state == 'EMPTY'):
            for obs in self.redoStackObservers:
                obs.stackEmpty()
        else:
            for obs in self.redoStackObservers:
                obs.stackHasElements()
