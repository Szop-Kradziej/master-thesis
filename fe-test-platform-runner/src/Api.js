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

export function fetchGroups(projectName) {
    return fetch(backendUrl(`/${projectName}/groups`), {
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
    return uploadData(backendUrl("/project/description"), data)
}

export function uploadStageDescription(data) {
    return uploadData(backendUrl("/stage/description"), data)
}

export function uploadTestCaseFile(projectName, stageName, testCaseName, fileType, data) {
    return uploadData(backendUrl('/' + projectName + '/' + stageName + '/' + testCaseName + '/' + fileType), data)
}

export function uploadStudentBinary(data) {
    return uploadData(backendUrl("/upload/bin"), data)
}

export function uploadStudentReport(data) {
    return uploadData(backendUrl("/upload/report"), data)
}

export function uploadStudentCodeLink(data) {
    return uploadData(backendUrl("/upload/code"), data)
}

function uploadData(url, data) {
    return axios.post(url, data)
        .then(function (response) {
            console.log("success");
        })
}

export function downloadProjectDescription(projectName) {
    return downloadFile(backendUrl('/' + projectName + "/description"))
}

export function downloadStageDescription(projectName, stageName) {
    return downloadFile(backendUrl('/' + projectName + '/' + stageName + "/description"))
}

export function downloadBin(projectName, stageName) {
    return downloadFile(backendUrl('/student/' + projectName + '/' + stageName + "/bin"))
}

export function downloadReport(projectName, stageName) {
    return downloadFile(backendUrl('/student/' + projectName + '/' + stageName + "/report"))
}

export function downloadTestCaseFile(projectName, stageName, testCaseName, fileType) {
    return downloadFile(backendUrl('/' + projectName + '/' + stageName + '/' + testCaseName + '/' + fileType))
}

export function downloadStudentLogsFile(projectName, stageName, testCaseName) {
    return downloadFile(backendUrl('/student/' + projectName + '/' + stageName + '/' + testCaseName + '/logs'))
}

export function downloadFile(url) {
    return axios.get(url, {responseType: "blob"})
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

export function deleteTestCase(projectName, stageName, testCaseName) {
    return deleteItem(backendUrl('/' + projectName + '/' + stageName + '/' + testCaseName))
}

export function deleteStage(projectName, stageName) {
    return deleteItem(backendUrl('/' + projectName + '/' + stageName))
}

export function deleteProject(projectName) {
    return deleteItem(backendUrl('/' + projectName))
}

export function deleteGroup(projectName, groupName) {
    return deleteItem(backendUrl('/' + projectName + '/groups/' + groupName))
}

function deleteItem(url) {
    return axios.delete(url)
        .then(function (response) {
            console.log("success");
        })
}