import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../_services/task.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from 'src/app/_services/user.service';
import { User } from 'src/app/models/user-roles';

@Component({
  selector: 'app-task-details',
  templateUrl: './task-details.component.html',
  styleUrls: ['./task-details.component.css']
})
export class TaskDetailsComponent implements OnInit {
  currentTask = null;
  message = '';
  users: User[] = [];

  constructor(
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService) { }

  ngOnInit(): void {
    this.message = '';
    this.getTask(this.route.snapshot.paramMap.get('id'));
    this.getUsers();
  }

  getTask(id): void {
    this.taskService.get(id)
      .subscribe(
        data => {
          this.currentTask = data;
          this.currentTask.eta = new Date(this.currentTask.eta);
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
      assigned: this.currentTask.assigned,
      points: this.currentTask.points,
      resolved: status
    };

    this.taskService.update(this.currentTask.id, data)
      .subscribe(
        response => {
          this.currentTask.published = status;
          this.currentTask = response;
          console.log(response);
        },
        error => {
          console.log(error);
        });
  }

  updateTask(): void {
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
