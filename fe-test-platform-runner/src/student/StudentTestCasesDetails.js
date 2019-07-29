import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import StudentTestCaseRow from "./StudentTestCaseRow";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import Table from "@material-ui/core/Table/Table";
import TableCell from "@material-ui/core/TableCell/TableCell";
import TableBody from "@material-ui/core/TableBody/TableBody";

class StudentTestCasesDetails extends Component {

    render() {
        return (
            <div className={this.props.classes.root}>
                {this.isAnyTestCaseExist() ? this.renderTestCasesTable() : this.renderNoTestsLabel()}
            </div>
        );
    }

    isAnyTestCaseExist() {
        return this.props.testCases && this.props.testCases.length > 0;
    }

    renderTestCasesTable() {
        return <Table width="1700">
            <TableHead>
                <TableRow>
                    <CustomTableCell>
                        Nazwa:
                    </CustomTableCell>
                    <CustomTableCell>
                        Status:
                    </CustomTableCell>
                    <CustomTableCell>
                        Komunikat błędu:
                    </CustomTableCell>
                    <CustomTableCell>
                        Logi:
                    </CustomTableCell>
                    <CustomTableCell>
                        Parametry:
                    </CustomTableCell>
                    <CustomTableCell>
                        Plik wejściowy:
                    </CustomTableCell>
                    <CustomTableCell>
                        Plik wyjściowy:
                    </CustomTableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {this.props.testCases.map(testCase => (
                        <StudentTestCaseRow
                            testCase={testCase}
                            projectName={this.props.projectName}
                            stageName={this.props.stageName}/>
                    )
                )}
            </TableBody>
        </Table>;
    }

    renderNoTestsLabel() {
        return (
            <p className={this.props.classes.noTestText}>
                Brak testów
            </p>
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
        display: "flex",
        width: 1700,
    },
    button: {
        backgroundColor: "#5aa724",
        color: "black",
        marginTop: 20
    },
    noTestText: {
        fontSize: 12
    }
});

export default withStyles(styles)(StudentTestCasesDetails);
