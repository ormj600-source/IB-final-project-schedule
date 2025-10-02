------------------------------------------------------------------------
PROJECT TITLE: IB Subject Scheduler
------------------------------------------------------------------------
PURPOSE OF PROJECT:
Provide an interactive desktop wizard that helps IB students capture their
classes, priorities, and study availability, then generates a recommended
weekly study schedule they can review and revise later.
------------------------------------------------------------------------
VERSION or DATE: Version 1.0
Last updated: 2-10-2025
------------------------------------------------------------------------
HOW TO START THIS PROJECT:
1. Install a Java Development Kit (JDK) version 11 or newer and ensure the
   `javac` and `java` commands are available on your terminal PATH.
2. Open a terminal and change into the project root (the folder containing
   this README):
   - Windows PowerShell: `cd path\to\schedulingappimproved`
   - macOS/Linux shell: `cd /path/to/schedulingappimproved`
3. Compile the sources:
   `javac *.java`
4. Launch the application from the compiled classes:
   `java Menu`
   The main window opens with the menu. Create a schedule to populate the
   viewer, or open an existing one to edit.
------------------------------------------------------------------------
AUTHORS:
Original author(s): <ormj600-source>)
------------------------------------------------------------------------
USER INSTRUCTIONS:
- "Create new schedule" starts a guided wizard. Complete each step and press
  Finish to save the plan.
- "View current schedule" shows the most recently saved schedule in a table.
  Hover any cell to see the full hours/minutes allocation.
- "Update information" reloads the wizard with the saved data so you can make
  tweaks and generate a refreshed plan.
- Exit returns control to your desktop.
------------------------------------------------------------------------
