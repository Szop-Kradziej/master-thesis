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

export function fetchIntegrations(projectName) {
    return fetch(backendUrl(`/${projectName}/integrations`), {
        method: "GET",
        credentials: "include"
    })
}

export function addNewStage(projectName, stageName, startDate, endDate, points) {
    return fetch(backendUrl(`/stage`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&stageName=" + stageName + "&startDate=" + startDate + "&endDate=" + endDate + "&pointsNumber=" + points
    })
}

export function addNewIntegration(projectName, integrationName) {
    return fetch(backendUrl(`/${projectName}/integrations`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "integrationName=" + integrationName
    })
}

export function addNewGroup(projectName, groupName) {
    return fetch(backendUrl(`/group`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&groupName=" + groupName
    })
}

export function addNewStudent(projectName, groupName, studentName) {
    return fetch(backendUrl(`/group/student`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&groupName=" + groupName + "&studentName=" + studentName
    })
}

export function uploadProjectDescription(data) {
    return postData(backendUrl("/project/description"), data)
}

export function uploadGroupsFile(projectName, data) {
    return postData(backendUrl("/" + projectName + "/groups"), data)
}

export function uploadStageDescription(data) {
    return postData(backendUrl("/stage/description"), data)
}

export function uploadTestCaseFile(projectName, stageName, testCaseName, fileType, data) {
    return postData(backendUrl('/' + projectName + '/' + stageName + '/' + testCaseName + '/' + fileType), data)
}

export function uploadStudentBinary(data) {
    return postData(backendUrl("/upload/bin"), data)
}

export function uploadStudentReport(data) {
    return postData(backendUrl("/upload/report"), data)
}

export function uploadStudentCodeLink(data) {
    return postData(backendUrl("/upload/code"), data)
}

export function editStageStartDate(data) {
    return postData(backendUrl("/stage/startDate"), data)
}

export function editStageEndDate(data) {
    return postData(backendUrl("/stage/endDate"), data)
}

export function editStagePointsNumber(data) {
    return postData(backendUrl("/stage/pointsNumber"), data)
}

export function editTestCaseParameters(data) {
    return postData(backendUrl("/testCase/parameters"), data)
}

function postData(url, data) {
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

export function deleteIntegration(projectName, integrationName) {
    return fetch(backendUrl('/integration'), {
        method: "DELETE",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&integrationName=" + integrationName
    })
}

export function deleteGroup(projectName, groupName) {
    return fetch(backendUrl(`/group`), {
        method: "DELETE",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&groupName=" + groupName
    })
}

function deleteItem(url) {
    return axios.delete(url)
        .then(function (response) {
            console.log("success");
        })
}

export function removeStudentFromGroup(projectName, groupName, studentName) {
    return fetch(backendUrl(`/group/student`), {
        method: "DELETE",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&groupName=" + groupName + "&studentName=" + studentName
    })
}
