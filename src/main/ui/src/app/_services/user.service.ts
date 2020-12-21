import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalaryModel, User } from '../models/user-roles';
import { of } from 'rxjs/internal/observable/of';

const API_URL = 'http://localhost:8080/api/test/';
const USERS_URL = 'http://localhost:8080/api';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getPublicContent(): Observable<any> {
    return this.http.get(API_URL + 'all', { responseType: 'text' });
  }

  getUserBoard(): Observable<any> {
    return this.http.get(API_URL + 'user', { responseType: 'text' });
  }

  getModeratorBoard(): Observable<any> {
    return this.http.get(API_URL + 'mod', { responseType: 'text' });
  }

  getAdminBoard(): Observable<any> {
    return this.http.get(API_URL + 'admin', { responseType: 'text' });
  }

  getAllUsers(): Observable<any> {
    return this.http.get(`${USERS_URL}/users`, httpOptions);
  }

  getUser(userId): Observable<User> {
    return this.http.get<User>(`${USERS_URL}/users/${userId}`, httpOptions);
  }

  getUserByUsername(username): Observable<User> {
    return this.http.get<User>(`${USERS_URL}/user/${username}`, httpOptions);
  }

  editeUser(user: any): Observable<any> {
    return this.http.post(`${USERS_URL}/user_update`, user, httpOptions);
  }

  deleteUser(user: any): Observable<any> {
    return this.http.post(`${USERS_URL}/user_delete`, user, httpOptions);
  }

  addBonus(userId, comment, date: Date, amount): Observable<any> {
    const data = {
      userId: userId.toString(),
      comment,
      month: (date.getMonth() + 1).toString(),
      amount: amount.toString(),
      year: date.getFullYear().toString()
    }
    return this.http.post(`${USERS_URL}/add_bonus`, data, httpOptions);
  }

  getBonuses(userId, month, year): Observable<any> {
    return this.http.get(`${USERS_URL}/bonus/${userId}/${month}/${year}`, httpOptions);
  }

  getSalary(userId, month, year): Observable<SalaryModel> {
    return this.http.get<SalaryModel>(`${USERS_URL}/salary/${userId}/${month}/${year}`, httpOptions);
  }

}
