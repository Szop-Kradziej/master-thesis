import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import PreviewStudentTestCaseRow from "./PreviewStudentIntegrationTestCaseRow";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../../styles/ProjectBoardStyles";
import TableBody from "@material-ui/core/TableBody/TableBody";

class PreviewStudentIntegrationTestCasesDetails extends Component {

    render() {
        return (
            <div className={this.props.classes.root_2}>
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
                        <PreviewStudentTestCaseRow
                            testCase={testCase}
                            groupName={this.props.groupName}
                            projectName={this.props.projectName}
                            taskName={this.props.taskName}
                            taskType={this.props.taskType}/>
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

// const CustomTableCell = withStyles(() => ({
//     head: {
//         color: "black",
//         fontWeight: 700,
//         fontSize: 12,
//         margin: 0,
//         padding: 0,
//         border: 0
//     },
//     body: {
//         color: "black",
//         fontSize: 12,
//         margin: 0,
//         padding: 0,
//         border: 0,
//         height: 5
//     }
// }))(TableCell);
//
// const styles = theme => ({
//     root: {
//         display: "flex",
//         width: 1700,
//     },
//     button: {
//         backgroundColor: "#5aa724",
//         color: "black",
//         marginTop: 20
//     },
//     noTestText: {
//         fontSize: 12
//     }
// });

export default withStyles(styles)(PreviewStudentIntegrationTestCasesDetails);
