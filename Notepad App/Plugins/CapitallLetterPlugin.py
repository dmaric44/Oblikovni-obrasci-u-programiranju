from Plugins.Plugin import Plugin


class CapitallLetterPlugin(Plugin):
    def getName(self):
        return "Capital letter"

    def getDescription(self):
        return "Change every first letter of word in capital"

    def execute(self, model, undoManager, clipboardStack):
        newLines = []
        for line in model.getLines():
            text = ""
            for w in line.split(" "):
                text += w.capitalize() + " "
            # print(text)
            newLines.append(text)
        model.lines = newLines
