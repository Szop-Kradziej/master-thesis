import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {Route, Router, Switch} from 'react-router-dom';
import customHistory from "./history";
import ProjectsBoard from "./ProjectsBoard";
import ProjectBoard from "./ProjectBoard";


const TopRoute = () => (
    <Switch>
        <Route exact path='/' component={App}/>
        <Route exact path='/projects' component={ProjectsBoard}/>
        <Route path="/projects/:projectId" component={ProjectBoard}/>
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