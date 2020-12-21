import { Component, OnInit } from '@angular/core';
import { TaskStatuses, TaskStatusesModel } from 'src/app/models/task.model';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import { UserService } from 'src/app/_services/user.service';
import { TaskService } from '../../_services/task.service';

@Component({
  selector: 'app-tasks-list',
  templateUrl: './tasks-list.component.html',
  styleUrls: ['./tasks-list.component.css']
})
export class TasksListComponent implements OnInit {

  tasks: any;
  currentTask = null;
  currentIndex = -1;
  title = '';
  currentUser = null;
  isTaskEditMode = false;

  showAdminBoard = false;
  showModeratorBoard = false;

  form = {
    comment: ''
  }
  user;
  comments;
  newTaskTitle;

  constructor(private taskService: TaskService,
              private tokenStorageService: TokenStorageService,
              private userService: UserService) { }

  ngOnInit(): void {
    this.retrieveTasks();

    if (!!this.tokenStorageService.getToken()) {
      this.user = this.tokenStorageService.getUser();
      this.showAdminBoard = this.user.roles.includes('ROLE_ADMIN');
      this.showModeratorBoard = this.user.roles.includes('ROLE_MODERATOR');
    }
  }

  getUserDetails(username): void {
    this.userService.getUserByUsername(username)
      .subscribe(
        data => {
          this.currentUser = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  retrieveTasks(): void {
    this.taskService.getAll()
      .subscribe(
        data => {
          if (this.newTaskTitle) {
            this.currentTask = data.find( (value, index) => {
              if (value.taskTitle === this.newTaskTitle) {
                this.currentIndex = index;
                return true;
              }
              return false;
            });
            console.log(this.currentTask);
            this.newTaskTitle = '';
          }
          this.tasks = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  refreshList(): void {
    this.retrieveTasks();
    this.currentTask = null;
    this.currentIndex = -1;
  }

  setActiveTask(task, index): void {
    this.getTask(task.id, index);
  }

  getTask(id, index): void {
    this.currentIndex = index;
    this.taskService.get(id).subscribe( newTask => {
      this.getUserDetails(newTask.assigned);
      this.currentTask = newTask;
      this.currentTask.comments = this.currentTask.comments.map( comment => {
        comment.date = new Date(comment.date);
        return comment;
      });
    });
  }

  searchTitle(): void {
    this.taskService.findByTitle(this.title)
      .subscribe(
        data => {
          this.tasks = data;
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  onTaskCreate(title): void {
    this.newTaskTitle = title;
    this.retrieveTasks();
    this.isTaskEditMode = false;
  }

  addComment(): void {
    const comment = {
      taskId: this.currentTask.id.toString(),
      userId: this.user.id.toString(),
      comment: this.form.comment.toString(),
      date: new Date().toString()
    };

    this.taskService.addTaskComment(comment).subscribe(() => {
      this.form.comment = '';
      this.getTask(this.currentTask.id, this.currentIndex);
    });
  }

  getStatusColorClass(status): string {
    if (status === TaskStatusesModel.New) {
      return 'circle-grey';
    } else if (status === TaskStatusesModel.In_progress) {
      return 'circle-blue';
    } else {
      return 'circle-green';
    }
  }
}
