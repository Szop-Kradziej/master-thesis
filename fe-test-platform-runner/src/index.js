import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {Route, Router, Switch} from 'react-router-dom';
import customHistory from "./history";
import ProjectsBoard from "./lecturer/management/ProjectsBoard";
import ProjectBoard from "./lecturer/management/project/ProjectBoard";
import StudentProjectsBoard from "./student/StudentProjectsBoard";
import StudentProjectBoard from "./student/StudentProjectBoard";
import PreviewBoard from "./lecturer/preview/PreviewBoard";
import PreviewProjectsBoard from "./lecturer/preview/PreviewProjectsBoard";
import PreviewStudentProjectBoard from "./lecturer/preview/PreviewStudentProjectBoard";

const TopRoute = () => (
    <Switch>
        <Route exact path='/' component={App}/>
        <Route exact path='/projects' component={ProjectsBoard}/>
        <Route path="/projects/:projectId" component={ProjectBoard}/>
        <Route exact path="/preview" component={PreviewProjectsBoard}/>
        <Route exact path="/preview/student/:projectId/:groupId" component={PreviewStudentProjectBoard}/>
        <Route path="/preview/:projectId" component={PreviewBoard}/>
        <Route exact path='/student/projects/' component={StudentProjectsBoard}/>
        <Route path="/student/projects/:projectId" component={StudentProjectBoard}/>
    </Switch>
);

const router = (
    <Router history={customHistory}>
        <Route component={TopRoute}/>
    </Router>
);

ReactDOM.render(router, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();