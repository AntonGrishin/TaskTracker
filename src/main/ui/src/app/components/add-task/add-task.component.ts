import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user-roles';
import { UserService } from 'src/app/_services/user.service';
import { TaskService } from '../../_services/task.service';

@Component({
  selector: 'app-add-task',
  templateUrl: './add-task.component.html',
  styleUrls: ['./add-task.component.css']
})
export class AddTaskComponent implements OnInit {
  task = {
    taskTitle: '',
    taskDescription: '',
    startDate: '',
    eta: '',
    assigned: '',
    points: '',
    resolved: false
  };
  submitted = false;
  users: User[];

  constructor(private taskService: TaskService,
              private userService: UserService) { }

  ngOnInit(): void {
    this.getUsers();
  }

  saveTask(): void {
    const data = {
      taskTitle: this.task.taskTitle,
      taskDescription: this.task.taskDescription,
      startDate: this.task.startDate,
      eta: this.task.eta,
      assigned: this.task.assigned,
      points: this.task.points
    };

    this.taskService.create(data)
      .subscribe(
        response => {
          console.log(response);
          this.submitted = true;
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

  newTask(): void {
    this.submitted = false;
    this.task = {
      taskTitle: '',
      taskDescription: '',
      startDate: '',
      eta: '',
      assigned: '',
      points: '',
      resolved: false
    };
  }

}
