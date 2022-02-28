
class ClipboardStack:

    def __init__(self):
        self.texts = []
        self.clipboardObservers = []

    def addClipboardObserver(self, obs):
        if(obs not in self.clipboardObservers):
            self.clipboardObservers.append(obs)

    def removeClipboardObserver(self, obs):
        self.clipboardObservers.remove(obs)

    def notifyObservers(self):
        for obs in self.clipboardObservers:
            obs.updateClipboard()

    def hasText(self):
        if(self.texts is not None and len(self.texts)>0):
            return True
        return False

    def getText(self):
        if(self.hasText()):
            return self.texts[len(self.texts)-1]

    def removeText(self):
        if(self.hasText()):
            text = self.texts.pop()
            self.notifyObservers()
            return text

    def putText(self, text):
        self.texts.append(text)
        self.notifyObservers()