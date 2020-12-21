import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../_services/task.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from 'src/app/_services/user.service';
import { User, userRoles } from 'src/app/models/user-roles';
import { TaskStatuses, TaskStatusesModel } from 'src/app/models/task.model';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {
  currentTask = null;
  message = '';
  users: User[] = [];
  taskStatuses = TaskStatuses;
  currentUser: User;
  private roles: string[];

  isAdmin = null;
  isModerator = null;

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private tokenStorageService: TokenStorageService
    ) { }

  ngOnInit(): void {
    this.message = '';
    this.getTask(this.route.snapshot.paramMap.get('id'));
    this.getUsers();

    const user = this.tokenStorageService.getUser();
    this.roles = user.roles;
  
    this.isAdmin = this.roles.includes('ROLE_ADMIN');
    this.isModerator = this.roles.includes('ROLE_MODERATOR');
  }

  getTask(id): void {
    this.taskService.get(id)
      .subscribe(
        data => {
          this.currentTask = data;
          this.currentTask.eta = new Date(this.currentTask.eta);
          this.currentTask.startDate = new Date(this.currentTask.startDate);
          console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  getUsers() {
    this.userService.getAllUsers().subscribe(users => {
      this.users = users;
    });
  }

  updateResolved(status): void {
    const data = {
      taskTitle: this.currentTask.taskTitle,
      taskDescription: this.currentTask.taskDescription,
      eta: this.currentTask.eta,
      startDate: this.currentTask.startDate,
      assigned: this.currentTask.assigned,
      points: this.currentTask.points,
      resolved: status,
      status: TaskStatusesModel.Resolved
    };

    this.taskService.update(this.currentTask.id, data)
      .subscribe(
        response => {
          this.currentTask.published = status;
          this.currentTask = response;
          this.currentTask.eta = new Date(this.currentTask.eta);
          this.currentTask.startDate = new Date(this.currentTask.startDate);
          console.log(response);
        },
        error => {
          console.log(error);
        });
  }

  updateTask(): void {
    this.currentTask.resolved = this.currentTask.status ===  TaskStatusesModel.Resolved;
    this.taskService.update(this.currentTask.id, this.currentTask)
      .subscribe(
        response => {
          console.log(response);
          this.message = 'The task was updated successfully!';
        },
        error => {
          console.log(error);
        });
  }

  deleteTask(): void {
    this.taskService.delete(this.currentTask.id)
      .subscribe(
        response => {
          console.log(response);
          this.router.navigate(['/tasks']);
        },
        error => {
          console.log(error);
        });
  }
}
