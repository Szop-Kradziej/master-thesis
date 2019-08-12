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
        credentials: "include",
    })
}

export function fetchStudentProjects() {
    return fetch(backendUrl(`/student/projects`), {
        method: "GET",
        credentials: "include",
        headers: {'Authorization': getAuthHeader()}
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

export function addNewStage(projectName, stageName, startDate, endDate) {
    return fetch(backendUrl(`/stage`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: "projectName=" + projectName + "&stageName=" + stageName + "&startDate=" + startDate + "&endDate=" + endDate
    })
}

export function addNewIntegration(projectName, data) {
    return fetch(backendUrl(`/${projectName}/integrations`), {
        method: "POST",
        credentials: "include",
        headers: {'Content-Type': 'application/json'},
        body: data
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

export function uploadProjectFile(data, type) {
    return postData(backendUrl('/project/' + type), data)
}

export function uploadGroupsFile(projectName, data) {
    return postData(backendUrl("/" + projectName + "/groups"), data)
}

export function uploadStageDescription(data) {
    return postData(backendUrl("/stage/description"), data)
}

export function uploadStageTestCaseFile(projectName, stageName, testCaseName, fileType, data) {
    return postData(backendUrl('/stage/' + projectName + '/' + stageName + '/' + testCaseName + '/' + fileType), data)
}

export function uploadIntegrationTestCaseFile(projectName, integrationName, testCaseName, fileType, data) {
    return postData(backendUrl('/integration/' + projectName + '/' + integrationName + '/' + testCaseName + '/' + fileType), data)
}

export function uploadStudentBinary(data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/student/upload/bin"), data, headers)
}

export function uploadStudentReport(data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/student/upload/report"), data, headers)
}

export function uploadStudentCodeLink(data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/student/upload/code"), data, headers)
}

export function uploadPreviewStudentBinary(groupName, data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/preview/" + groupName + "/upload/bin"), data, headers)
}

export function uploadPreviewStudentReport(groupName, data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/preview/" + groupName + "/upload/report"), data, headers)
}

export function uploadPreviewStudentCodeLink(groupName, data) {
    let headers = {'Authorization': getAuthHeader()};
    return postData(backendUrl("/preview/" + groupName + "/upload/code"), data, headers)
}

export function editStageStartDate(data) {
    return postData(backendUrl("/stage/startDate"), data)
}

export function editStageEndDate(data) {
    return postData(backendUrl("/stage/endDate"), data)
}

export function editStageTestCaseParameters(data) {
    return postData(backendUrl("/stage/testCase/parameters"), data)
}

export function editIntegrationTestCaseParameters(data) {
    return postData(backendUrl("/integration/testCase/parameters"), data)
}

function postData(url, data, headers) {
    return axios.post(url, data, { headers: headers})
        .then(function (response) {
            console.log("success");
        })
}

export function downloadProjectDescription(projectName) {
    return downloadFile(backendUrl('/' + projectName + "/description"))
}

export function downloadProjectEnvironment(projectName) {
    return downloadFile(backendUrl('/' + projectName + "/environment"))
}

export function downloadStageDescription(projectName, stageName) {
    return downloadFile(backendUrl('/' + projectName + '/' + stageName + "/description"))
}

export function downloadStageStatistics(groupName, projectName, stageName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/stage/' + projectName + '/' + stageName + "/statistics"), headers)
}

export function downloadIntegrationStatistics(groupName, projectName, integrationName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/integration/' + projectName + '/' + integrationName + "/statistics"), headers)
}

export function downloadBin(projectName, stageName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/student/' + projectName + '/' + stageName + "/bin"), headers)
}

export function downloadReport(projectName, stageName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/student/' + projectName + '/' + stageName + "/report"), headers)
}

export function downloadPreviewBin(groupName, projectName, stageName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/' + projectName + '/' + stageName + "/bin"), headers)
}

export function downloadPreviewReport(groupName, projectName, stageName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/' + projectName + '/' + stageName + "/report"), headers)
}

export function downloadStageTestCaseFile(projectName, stageName, testCaseName, fileType) {
    return downloadFile(backendUrl('/stage/' + projectName + '/' + stageName + '/' + testCaseName + '/' + fileType))
}

export function downloadIntegrationTestCaseFile(projectName, integrationName, testCaseName, fileType) {
    return downloadFile(backendUrl('/integration/' + projectName + '/' + integrationName + '/' + testCaseName + '/' + fileType))
}

export function downloadStudentStageLogsFile(projectName, stageName, testCaseName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/student/stage/' + projectName + '/' + stageName + '/' + testCaseName + '/logs'), headers)
}

export function downloadStudentIntegrationLogsFile(projectName, integrationName, testCaseName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/student/integration/' + projectName + '/' + integrationName + '/' + testCaseName + '/logs'), headers)
}

export function downloadPreviewStudentStageLogsFile(groupName, projectName, stageName, testCaseName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/stage/' + projectName + '/' + stageName + '/' + testCaseName + '/logs'), headers)
}

export function downloadPreviewStudentIntegrationLogsFile(groupName, projectName, integrationName, testCaseName) {
    let headers = {'Authorization': getAuthHeader()};
    return downloadFile(backendUrl('/preview/' + groupName + '/integration/' + projectName + '/' + integrationName + '/' + testCaseName + '/logs'), headers)
}

export function downloadFile(url, headers) {
    return axios.get(url, {responseType: "blob", headers: headers})
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

export function deleteStageTestCase(projectName, stageName, testCaseName) {
    return deleteItem(backendUrl('/stage/' + projectName + '/' + stageName + '/' + testCaseName))
}

export function deleteIntegrationTestCase(projectName, integrationName, testCaseName) {
    return deleteItem(backendUrl('/integration/' + projectName + '/' + integrationName + '/' + testCaseName))
}

export function deleteStage(projectName, stageName) {
    return deleteItem(backendUrl('/stage/' + projectName + '/' + stageName))
}

export function deleteProject(projectName) {
    return deleteItem(backendUrl('/' + projectName))
}

export function deleteIntegration(projectName, integrationName) {
    return deleteItem(backendUrl('/integration/' + projectName + '/' + integrationName))
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

export function getAuthHeader() {
    var user = localStorage.getItem("userName");
    var password = localStorage.getItem("token");

    var base64encodedData = new Buffer(user + ':' + password).toString('base64');

    return 'Basic ' + base64encodedData
}
