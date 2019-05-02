import React from 'react';
import backendUrl from "./backendUrl";

export function addNewProject(projectName) {
    return fetch(backendUrl(`/project`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName
    })
}

export function fetchProjects() {
    return fetch(backendUrl(`/projects`), {
        method: "GET",
        credentials: "include"
    })
}