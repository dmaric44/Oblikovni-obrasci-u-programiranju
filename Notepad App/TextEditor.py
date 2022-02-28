import datetime
import os
from functools import partial
from importlib import import_module
from tkinter import *
from tkinter import font as tkFont
from tkinter.filedialog import asksaveasfile, askopenfile

from ClipboardObserver import ClipboardObserver
from ClipboardStack import ClipboardStack
from CursorObserver import CursorObserver
from StackObserver import StackObserver
from TextEditorModel import *
from TextObserver import TextObserver
from UndoManager import UndoManager


class TextEditor(Tk):
    cursor = None
    prevKey = None

    def __init__(self):
        Tk.__init__(self)
        self.ctrlFlag = False

        # self.bind('<Key>', self.key)
        # self.addListeners()

        self.geometry("400x500+700+100")
        self.model = TextEditorModel("Sto je danas lijep i suncan dan\nDanas mi je rodendan.\nDal se sjetis nekad mene, dal se sjetis....")
        self.clipboard = ClipboardStack()
        self.undoManager = UndoManager()

        self.undoManager.addUndoStackObservers(UndoObserver(self))
        self.undoManager.addRedoStackObservers(RedoObserver(self))
        self.clipboard.addClipboardObserver(ClipObserver(self))
        self.statusBarObserver = StatusBarObserver(self)
        self.model.attachCursorObserver(self.statusBarObserver)
        self.model.attachTextObserver(self.statusBarObserver)

        self.textEditorPanel = TextEditorPanel(self)
        self.textEditorPanel.pack()

        self.keyBindings()
        self.clock()
        self.plugins = self.importPlugins()


    def importPlugins(self):
        for mymodule in os.listdir('Plugins'):
            name, ext = os.path.splitext(mymodule)
            if(ext == '.py'):
                plugin = myfactory(name)
                self.textEditorPanel.plugins.add_command(label=plugin.getName(plugin), command = partial(plugin.execute,plugin, self.model, self.undoManager, self.clipboard))



    def goLeft(self, event):
        self.model.moveCursorLeft()
        self.model.selectionRange = None
    def goRight(self, event):
        self.model.moveCursorRight()
        self.model.selectionRange = None
    def goUp(self, event):
        self.model.moveCursorUp()
        self.model.selectionRange = None
    def goDown(self, event):
        self.model.moveCursorDown()
        self.model.selectionRange = None

    def ins(self, event):
        # print(event.keycode)
        if(not self.ctrlFlag and event.keycode >= 65 and event.keycode <= 90):
            self.model.insert(event.keysym)
        self.ctrlFlag = False



    def deleteBackSpace(self, event):
        if(self.model.hasSelection()):
            self.model.deleteRange()
        else:
            self.model.deleteBefore()

    def deleteDelete(self, event):
        if (self.model.hasSelection()):
            self.model.deleteRange()
        else:
            self.model.deleteAfter()

    def ctrlC(self, event):
        self.ctrlFlag = True
        if(self.model.hasSelection()):
            text = self.model.getSelectionText()
            self.clipboard.putText(text)
    def copy(self):
        self.ctrlFlag = True
        if(self.model.hasSelection()):
            text = self.model.getSelectionText()
            self.clipboard.putText(text)

    def ctrlX(self, event):
        self.ctrlFlag = True
        if(self.model.hasSelection()):
            text = self.model.getSelectionText()
            self.clipboard.putText(text)
            self.model.deleteRange()

    def cut(self):
        self.ctrlFlag = True
        if (self.model.hasSelection()):
            text = self.model.getSelectionText()
            self.clipboard.putText(text)
            self.model.deleteRange()

    def ctrlV(self, event):
        self.ctrlFlag = True
        text = self.clipboard.getText()
        self.model.insert(text)

    def paste(self):
        self.ctrlFlag = True
        text = self.clipboard.getText()
        self.model.insert(text)

    def ctrlShV(self, event):
        self.ctrlFlag = True
        text = self.clipboard.removeText()
        self.model.insert(text)

    def pasteAndTake(self):
        self.ctrlFlag = True
        text = self.clipboard.removeText()
        self.model.insert(text)

    def ctrlZ(self, event):
        self.ctrlFlag = True
        self.undoManager.undo()

    def ctrlY(self, event):
        self.ctrlFlag = True
        self.undoManager.redo()

    def keyBindings(self):
        self.bind('<Up>', self.goUp)
        self.bind('<Down>', self.goDown)
        self.bind('<Left>', self.goLeft)
        self.bind('<Right>', self.goRight)

        self.bind('<Shift-Left>', lambda x: self.model.addSelectionLeft())
        self.bind('<Shift-Right>', lambda x: self.model.addSelectionRight())
        self.bind('<Shift-Up>', lambda x: self.model.addSelectionUp())
        self.bind('<Shift-Down>', lambda x: self.model.addSelectionDown())

        self.bind('<BackSpace>', self.deleteBackSpace)
        self.bind('<Delete>', self.deleteDelete)

        self.bind('<Control-c>', self.ctrlC)
        self.bind('<Control-x>', self.ctrlX)
        self.bind('<Control-v>', self.ctrlV)
        self.bind('<Control-b>', self.ctrlShV)

        self.bind('<Control-z>', self.ctrlZ)
        self.bind('<Control-y>', self.ctrlY)

        self.bind('<Return>', lambda x: self.model.insert(10))
        self.bind('<space>', lambda x:self.model.insert(32))
        self.bind('<KeyRelease>', self.ins)

    def clock(self):
        time = datetime.datetime.now().strftime("Time: %H:%M:%S")
        self.textEditorPanel.paintComponent()
        self.after(400, self.clock)

    def save(self):
        files = [('Text Document', '*.txt'), ('All Files', '*.*'), ('Python Files', '*.py')]
        file = asksaveasfile(mode='w', filetypes=files, defaultextension=files)
        for i in self.model.lines:
            file.write(i + "\n")

    def open(self):
            file = askopenfile(mode='r', filetypes=[('Text Document', '*.txt'), ('All Files', '*.*'), ('Python Files', '*.py')])
            if file is not None:
                content = file.read()
                # self.model = TextEditorModel(content)
                self.model.lines = content.split("\n")
                self.model.notifyTextObservers()

    def clearDocument(self):
        self.model.lines = [" "]
        self.model.moveCursorToStart()
        self.model.notifyTextObservers()



class TextEditorPanel(Frame):
    IN_LINE = 15
    MARGIN = 3

    def __init__(self, master):
        super().__init__()
        self.master = master
        self.master.title("Text Editor")
        self.state = 'diabled'

        self.menubar = Menu(self)
        # File
        self.file = Menu(self.menubar, tearoff=0)
        self.menubar.add_cascade(label="File", menu=self.file)
        self.file.add_command(label = "Open", command = self.master.open)
        self.file.add_command(label="Save",command = self.master.save)
        self.file.add_separator()
        self.file.add_command(label="Exit", command= lambda : exit(1))
        # Edit
        self.edit = Menu(self.menubar, tearoff=0)
        self.menubar.add_cascade(label="Edit", menu=self.edit)
        self.edit.add_command(label="Undo", command=self.master.undoManager.undo, state='disabled')
        self.edit.add_command(label="Redo",command=self.master.undoManager.redo, state='disabled' )
        self.edit.add_command(label="Cut", command=self.master.cut, state='disabled')
        self.edit.add_command(label="Copy", command=self.master.copy, state='disabled')
        self.edit.add_command(label="Paste", command=self.master.paste, state='disabled')
        self.edit.add_command(label="Paste and take", command=self.master.pasteAndTake, state='disabled')
        self.edit.add_command(label="Delete selection", command=self.master.model.deleteRange, state='disabled')
        self.edit.add_command(label="Clear document", command=self.master.clearDocument)
        # Move
        self.move = Menu(self.menubar, tearoff=0)
        self.menubar.add_cascade(label="Move", menu=self.move)
        self.move.add_command(label="Cursor to document start", command = self.master.model.moveCursorToStart)
        self.move.add_command(label="Cursor to document end", command = self.master.model.moveCursorToEnd)
        # Plugins
        self.plugins = Menu(self.menubar, tearoff=0)
        self.menubar.add_cascade(label="Plugins", menu=self.plugins)



        self.toolbar = Frame()
        self.toolbar.pack(side='top', fill = 'x', expand=False)
        self.b1 = Button(self.toolbar, text="Undo", command=self.master.undoManager.undo, state='disabled')
        self.b2 = Button(self.toolbar, text="Redo", command=self.master.undoManager.redo, state='disabled')
        self.b3 = Button(self.toolbar, text="Cut", command=self.master.cut, state='disabled')
        self.b4 = Button(self.toolbar, text="Copy", command=self.master.copy, state='disabled')
        self.b5 = Button(self.toolbar, text="Paste", command=self.master.paste, state='disabled')
        self.b1.pack(side="left")
        self.b2.pack(side="left")
        self.b3.pack(side="left")
        self.b4.pack(side="left")
        self.b5.pack(side="left")
        self.pack(fill=BOTH, expand=1)
        self.canvas = Canvas(self, bg="white")
        self.cursor = master.model.getCursorLocation()
        self.master.config(menu = self.menubar)


        self.statusBar = Label(self, text=self.master.statusBarObserver.text, anchor='nw')
        self.statusBar.pack(side=BOTTOM, fill=X)


    def paintComponent(self):
        self.cursor = self.master.model.getCursorLocation()
        self.canvas.delete("all")
        self.statusBar.config(text=self.master.statusBarObserver.text)


        it = self.master.model.allLines()
        i=0
        try:
            while(True):
                line = next(it)
                self.canvas.create_text(self.MARGIN, i*self.IN_LINE, text=line, anchor="nw", font=("Times New Roman",10))
                i += 1
        except:
            StopIteration

        lines = self.master.model.getLines()
        line = lines[self.cursor.getX()]
        # print(lines)
        # print(line[0:self.cursor.getY()])

        txt = tkFont.Font(family="Times New Roman", size=10)
        width = txt.measure(line[0:self.cursor.getY()])
        # print(width)
        # print(self.cursor.getX(), self.cursor.getY())

        x1 = self.cursor.getX()*self.IN_LINE + 15
        y1 = width + self.MARGIN
        x2 = x1 - 15
        y2 = y1
        self.canvas.create_line(y1, x1, y2, x2, width=1)


        # paint rectangle
        if(self.master.model.hasSelection()):
            if(self.state != 'normal'):
                self.state = 'normal'
                self.b3.config(state='normal')
                self.b4.config(state='normal')
                self.master.textEditorPanel.edit.entryconfig("Cut", state='normal')
                self.master.textEditorPanel.edit.entryconfig("Copy", state='normal')
                self.master.textEditorPanel.edit.entryconfig("Delete selection", state='normal')

            numOfLines = self.master.model.selectionRange.getNumOfLinesInRange()
            if(numOfLines == 0):
                x1 = self.master.model.selectionRange.getStart().getX()
                y1 = self.master.model.selectionRange.getStart().getY()
                x2 = self.master.model.selectionRange.getEnd().getX()
                y2 = self.master.model.selectionRange.getEnd().getY()
                line = lines[x1]
                begin = txt.measure(line[0:y1])
                width = txt.measure(line[y1:y2])
                self.canvas.create_rectangle(begin+self.MARGIN, x1*self.IN_LINE+15, begin+width+self.MARGIN, x2*self.IN_LINE, fill='blue')
            else:
                line = lines[self.master.model.selectionRange.getStart().getX()]
                begin = txt.measure(line[0:self.master.model.selectionRange.getStart().getY()])
                width = txt.measure(line[0:len(line)])
                self.canvas.create_rectangle(self.MARGIN+begin, self.master.model.selectionRange.getStart().getX()*self.IN_LINE+15, self.MARGIN+width,self.master.model.selectionRange.getStart().getX()*self.IN_LINE, fill="blue")
                for i in range(self.master.model.selectionRange.getStart().getX()+1, self.master.model.selectionRange.getEnd().getX()):
                    line = lines[i]
                    self.canvas.create_rectangle(self.MARGIN, i*self.IN_LINE+15, self.MARGIN+txt.measure(line[0:len(line)]), i*self.IN_LINE, fill='blue')
                line = lines[self.master.model.selectionRange.getEnd().getX()]
                self.canvas.create_rectangle(self.MARGIN, self.master.model.selectionRange.getEnd().getX()*self.IN_LINE+15, self.MARGIN+txt.measure(line[0:self.master.model.selectionRange.getEnd().getY()]), self.master.model.selectionRange.getEnd().getX()*self.IN_LINE, fill='blue')
        else:
            if(self.state != 'disabled'):
                self.state = 'disabled'
                self.b3.config(state='disabled')
                self.b4.config(state='disabled')
                self.master.textEditorPanel.edit.entryconfig("Cut", state='disabled')
                self.master.textEditorPanel.edit.entryconfig("Copy", state='disabled')
                self.master.textEditorPanel.edit.entryconfig("Delete selection", state='disabled')
        self.canvas.pack(fill=BOTH, expand=1)

class UndoObserver(StackObserver):
    def __init__(self, master):
        self.master = master
    def stackHasElements(self):
        self.master.textEditorPanel.b1.configure(state='normal')
        self.master.textEditorPanel.edit.entryconfig("Undo", state='normal')
    def stackEmpty(self):
        self.master.textEditorPanel.b1.configure(state='disabled')
        self.master.textEditorPanel.edit.entryconfig("Undo", state='disabled')


class RedoObserver(StackObserver):
    def __init__(self, master):
        self.master = master
    def stackHasElements(self):
        self.master.textEditorPanel.b2.configure(state='normal')
        self.master.textEditorPanel.edit.entryconfig("Redo", state='normal')
    def stackEmpty(self):
        self.master.textEditorPanel.b2.configure(state='disabled')
        self.master.textEditorPanel.edit.entryconfig("Redo", state='disabled')

class ClipObserver(ClipboardObserver):
    def __init__(self,master):
        self.master = master
    def updateClipboard(self):
        if(self.master.clipboard.hasText()):
            self.master.textEditorPanel.b5.configure(state='normal')
            self.master.textEditorPanel.edit.entryconfig("Paste", state='normal')
            self.master.textEditorPanel.edit.entryconfig("Paste and take", state='normal')
        else:
            self.master.textEditorPanel.b5.configure(state='disabled')
            self.master.textEditorPanel.edit.entryconfig("Paste", state='disabled')
            self.master.textEditorPanel.edit.entryconfig("Paste and take", state='disabled')

class StatusBarObserver(CursorObserver, TextObserver):
    def __init__(self, master):
        self.master = master
        self.numOfLines = len(self.master.model.getLines())
        self.cursor = Location(self.master.model.getCursorLocation().getX(), self.master.model.getCursorLocation().getY())
        self.text = ("Line: %d, Column: %d, Number of lines: %d" %(self.cursor.getX()+1, self.cursor.getY(),self.numOfLines))
        # self.master.model.attachCursorObserver()
        # self.master.model.attachTextObserver()

    def updateCursorLocation(self, location):
        self.cursor.setLocation(location.getX(), location.getY())
        self.numOfLines = len(self.master.model.getLines())
        self.text = ("Line: %d, Column: %d, Number of lines: %d" %(self.cursor.getX()+1, self.cursor.getY(),self.numOfLines))
        # print(self.text)
    def updateText(self):
        self.cursor.setLocation(self.master.model.getCursorLocation().getX(), self.master.model.getCursorLocation().getY())
        self.numOfLines = len(self.master.model.getLines())
        self.text = ("Line: %d, Column: %d, Number of lines: %d" %(self.cursor.getX()+1, self.cursor.getY(),self.numOfLines))
        # print(self.text)

def myfactory(module):
  mod = import_module("Plugins."+str(module))
  return getattr(mod, module)

def main():
    window = TextEditor()
    window.mainloop()

if __name__ == '__main__':
    main()