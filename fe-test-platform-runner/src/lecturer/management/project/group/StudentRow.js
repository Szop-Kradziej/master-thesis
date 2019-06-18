import React, {Component} from "react";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import TableRow from "@material-ui/core/TableRow/TableRow";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import * as Api from "../../../../Api";

class StudentRow extends Component {

    handleRemoveStudent = (event) => {
        event.preventDefault();

        Api.removeStudentFromGroup(this.props.projectName, this.props.groupName, this.props.studentName)
            .then(this.props.groupChangedHandler)
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell component="th" scope="row">
                    {this.props.studentName}
                </CustomTableCell>
                <CustomTableCell>
                    <CustomTableCell>
                        <IconButton aria-label="Usuń" onClick={this.handleRemoveStudent}>
                            <DeleteIcon/>
                        </IconButton>
                    </CustomTableCell>
                </CustomTableCell>
                <CustomTableCell/>
            </TableRow>
        );
    }
}

export default (StudentRow);
