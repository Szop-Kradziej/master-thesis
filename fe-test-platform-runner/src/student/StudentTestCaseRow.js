import React, {Component} from "react";
import {withStyles} from "@material-ui/core";

class StudentTestCaseRow extends Component {

    render() {
        return (
            <div className={this.props.classes.root}>
                <p>{this.props.testCaseName}</p>
            </div>
        );
    }
}

const styles = theme => ({
    root: {
        flexGrow: 1,
    },
});

export default withStyles(styles)(StudentTestCaseRow);
