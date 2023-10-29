# taskmaster
# Beginning TaskMaster

| Homepage | All Tasks |
|:---:|:---:|
| ![homepage](./screenshots/homelab29.png) | ![alltasks](./screenshots/AllTasks.png) |
| Add Task | Added Task |
| ![addtask](./screenshots/AddTasklab29.png) | ![addedtask](./screenshots/TaskAddedlab29.png) |
| user Settings | username Saved |
| ![user Settings](./screenshots/userSettings.png) | ![userName Saved](./screenshots/userNameSaved.png) |
| Select task from Homepage | Task Details |
| ![Task Selected](./screenshots/homelab29.png) | ![Task Details](./screenshots/TaskDetailslab29.png) |

| RoomDbWorking |
|:---:| 
| ![Task Selected](./screenshots/RoomDbWorking.png) |

## description
Task master mobile application with three pages:
1. home page with two buttons and image:
  - Add new Task Button
  - All Tasks Button
2. AllTasks page:
 - Image
 - back button that back to home page.

3. AddNewTask page:
  - Add task button that show Task added Toast when clicked.
  - task description
  - task title

4. user Settings page:
  - edit text for username.
  - save button.

5. Task details page:
  - task title based on the clicked task.
  - task description.
  - task state.

## daily change log
- Lab27
 1. Add Task Details page with two text views: task title and task description. Task title is passed from intent in the main when the task button clicked.

 2. Add Users Settings page with: edit text for the username and a save button. The username is passed using Shared Preferences to the home page.

- Lab28
1. Added a RecyclerView for displaying Task title, the tasks are hardcoded.

2. Created a Task class with attributes: title, body and state. The state is enum of type TaskState, and can be one of: “new”, “assigned”, “in progress”, or “complete”

- Lab29
1. Set up Room, and modify Task class to be an Entity.
2. Modify Add Task form to save the data entered in as a Task in local database.
3. Refactor homepage RecyclerView to display all Task entities from database.
