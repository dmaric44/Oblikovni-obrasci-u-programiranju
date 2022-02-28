from Plugins.Plugin import Plugin
from tkinter import messagebox

class StatisticsPlugin(Plugin):

    def getName(self):
        return "Statistics"

    def getDescription(self):
        return "Counts rows, words and letters in document"

    def execute(self, model, undoManager, clipboardStack):
        lines = model.getLines()

        cntRows = 0
        cntWords = 0
        cntLetters = 0

        for line in lines:
            thisLine = line.split(" ")
            for i in thisLine:
                cntWords += 1
                cntLetters += len(i)
            cntRows += 1

        text = ("Number of lines: %d\n" %cntRows)
        text += ("Number of words: %d\n" %cntWords)
        text += ("Number of letters %d\n" %cntLetters)

        messagebox.showinfo("Info", text)
