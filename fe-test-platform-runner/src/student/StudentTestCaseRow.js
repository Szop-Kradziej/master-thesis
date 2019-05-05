import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import green from "@material-ui/core/es/colors/green";
import red from "@material-ui/core/es/colors/red";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableCell from "@material-ui/core/TableCell/TableCell";

class StudentTestCaseRow extends Component {

    render() {
        return (
            <TableRow key="custom_key">
                <CustomTableCell component="th" scope="row" width="15%">
                    <p>{this.props.testCase.testCaseName}</p>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="5%">
                    <p><font
                        color={this.props.testCase.status === 'SUCCESS' ? "green" : "red"}>{this.props.testCase.status}</font>
                    </p>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="70%">
                    <p>{this.props.testCase.message ? this.props.testCase.message : "Brak"}</p>
                </CustomTableCell>
                <CustomTableCell>
                    <p>{this.props.testCase.logs ? this.props.testCase.logs : "Brak"}</p>
                    {/*TODO: Logi*/}
                    {/*<IconButton aria-label="Pobierz">*/}
                    {/*<DownloadIcon/>*/}
                    {/*</IconButton>*/}
                </CustomTableCell>
            </TableRow>
        );
    }
}

const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 12,
        margin: 0,
        padding: 0,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 12,
        margin: 0,
        padding: 0,
        border: 0,
        height: 5
    }
}))(TableCell);

const styles = theme => ({
    root: {
        flexGrow: 1,
        width: 1700
    },
});

export default withStyles(styles)(StudentTestCaseRow);
