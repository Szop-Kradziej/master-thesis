import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import green from "@material-ui/core/es/colors/green";
import red from "@material-ui/core/es/colors/red";

class StudentTestCaseRow extends Component {

    render() {
        return (
            <div className={this.props.testCase.status === 'SUCCESS' ? this.props.classes.rootSuccess : this.props.classes.rootFailure}>
                <p>{this.props.testCase.testCaseName}</p>
                <p>{this.props.testCase.status}</p>
                <p>{this.props.testCase.message}</p>
            </div>
        );
    }
}

const styles = theme => ({
    rootSuccess: {
        flexGrow: 1,
        backgroundColor: "green"
    },
    rootFailure: {
        flexGrow: 1,
        backgroundColor: "red"
    }
});

export default withStyles(styles)(StudentTestCaseRow);
