# taskmaster
# Beginning TaskMaster

| Homepage | All Tasks |
|:---:|:---:|
| ![homepage](./screenshots/homelab29.png) | ![alltasks](./screenshots/AllTasks.png) |
| Add Task | Added Task |
| ![addtask](./screenshots/AddTasklab32.png) | ![addedtask](./screenshots/TaskAddedlab32.png) |
| user Settings | username Saved |
| ![user Settings](./screenshots/userSettings.png) | ![userName Saved](./screenshots/userNameSaved.png) |
| Select task from Homepage | Task Details |
| ![Task Selected](./screenshots/homelab32.png) | ![Task Details](./screenshots/TaskDetailslab32.png) |

| DynamoDB Working |
|:---:| 
| ![Task Selected](./screenshots/DynamoDBWorking.png) |

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

- Lab31
1. Did Espresso tests for these Activities :
   - Main Activity.
   - user Settings/profile .
   - Add Task.

- Lab32
1. Remove Room dependencies and room connections from the application.
2. Remove Room form these Activities :
   - Main Activity. 
   - Add Task.
3. Added Amplify to the project successfully
4. Saving tasks in Dynamo db (Add Task Activity) and showing (retrieve) them in the main Activity.