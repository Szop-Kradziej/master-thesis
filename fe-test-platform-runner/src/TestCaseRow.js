import Button from "@material-ui/core/Button/Button";
import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import backendUrl from "./backendUrl";
import axios from "axios";
import Grid from "@material-ui/core/Grid/Grid";
import {saveAs} from "file-saver";

class TestCaseRow extends Component {

    constructor(props) {
        super(props);
    }

    handleDownloadInputFile = () => {
        this.downloadFile("input");
    };

    handleDownloadOutputFile = () => {
        this.downloadFile("output");
    };

    downloadFile = (fileName) => {
        console.log(backendUrl('/' + this.props.projectName + '/' + this.props.stageName + '/' + this.props.testCaseName + '/' + fileName));
        axios.get(backendUrl('/' + this.props.projectName + '/' + this.props.stageName + '/' + this.props.testCaseName + '/' + fileName), {responseType: "blob"})
            .then((response) => {
                console.log("Response", response);
                console.log("File name", fileName);
                saveAs(new Blob([response.data]), fileName);
            }).catch(function (error) {
            console.log(error);
            if (error.response) {
                console.log('Error', error.response.status);
            } else {
                console.log('Error', error.message);
            }
        });
    };

    render() {
        return (
            <div className={this.props.classes.root}>

                <Grid container spacing={24}>
                    <Grid item xs={4}>
                        <p>{this.props.testCaseName}</p>
                    </Grid>
                    <Grid item xs={4}>
                        <Button onClick={this.handleDownloadInputFile}>
                            INPUT
                        </Button>
                    </Grid>
                    <Grid item xs={4}>
                        <Button onClick={this.handleDownloadOutputFile}>
                            OUTPUT
                        </Button>
                    </Grid>
                </Grid>
            </div>
        );
    }
}

const styles = theme => ({
    root: {
        flexGrow: 1,
    },
});

export default withStyles(styles)(TestCaseRow);
