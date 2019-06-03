import React from 'react';
import backendUrl from "./backendUrl";
import axios from "axios";
import {saveAs} from "file-saver";

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

export function fetchStages(projectName) {
    return fetch(backendUrl(`/${projectName}/stages`), {
        method: "GET",
        credentials: "include"
    })
}

export function addNewStage(projectName, stageName) {
    return fetch(backendUrl(`/stage`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&stageName=" + stageName
    })
}

export function uploadProjectDescription(data) {
    return axios.post(backendUrl("/project/description"), data)
        .then(function (response) {
            console.log("success");
        })
}

export function downloadProjectDescription(projectName) {
    return axios.get(backendUrl('/' + projectName + "/description"), {responseType: "blob"})
        .then((response) => {
            console.log("Response", response);
            let fileName = 'download';
            const contentDisposition = response.headers['content-disposition'];
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
                if (fileNameMatch.length === 2)
                    fileName = fileNameMatch[1];
            }
            saveAs(new Blob([response.data]), fileName);
        }).catch(function (error) {
            console.log(error);
            if (error.response) {
                console.log('Error', error.response.status);
            } else {
                console.log('Error', error.message);
            }
        });
}

export function uploadStageDescription(data) {
    return axios.post(backendUrl("/stage/description"), data)
        .then(function (response) {
            console.log("success");
        })
}

export function downloadStageDescription(projectName, stageName) {
    return axios.get(backendUrl('/' + projectName + '/' + stageName + "/description"), {responseType: "blob"})
        .then((response) => {
            console.log("Response", response);
            let fileName = 'download';
            const contentDisposition = response.headers['content-disposition'];
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
                if (fileNameMatch.length === 2)
                    fileName = fileNameMatch[1];
            }
            saveAs(new Blob([response.data]), fileName);
        }).catch(function (error) {
            console.log(error);
            if (error.response) {
                console.log('Error', error.response.status);
            } else {
                console.log('Error', error.message);
            }
        });
}

export function downloadBin(projectName, stageName) {
    return axios.get(backendUrl('/student/' + projectName + '/' + stageName + "/bin"), {responseType: "blob"})
        .then((response) => {
            console.log("Response", response);
            let fileName = 'download';
            const contentDisposition = response.headers['content-disposition'];
            if (contentDisposition) {
                const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
                if (fileNameMatch.length === 2)
                    fileName = fileNameMatch[1];
            }
            saveAs(new Blob([response.data]), fileName);
        }).catch(function (error) {
            console.log(error);
            if (error.response) {
                console.log('Error', error.response.status);
            } else {
                console.log('Error', error.message);
            }
        });
}
