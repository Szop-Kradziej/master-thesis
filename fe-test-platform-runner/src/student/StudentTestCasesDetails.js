import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import StudentTestCaseRow from "./StudentTestCaseRow";

class StudentTestCasesDetails extends Component {

    render() {
        return (
            <div className={this.props.classes.panel}>
                {this.props.testCases.map(testCase => (
                        <StudentTestCaseRow
                            testCaseName={testCase}
                            projectName={this.props.projectName}
                            stageName={this.props.stageName}/>
                    )
                )}
            </div>
        );
    }
}

const styles = theme => ({
    root: {
        flexGrow: 1,
    },
    button: {
        backgroundColor: "#5aa724",
        color: "black",
        marginTop: 20
    },
});

export default withStyles(styles)(StudentTestCasesDetails);
