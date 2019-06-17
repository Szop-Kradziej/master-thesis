import React, {Component} from "react";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import TableRow from "@material-ui/core/TableRow/TableRow";
import DeleteIcon from "@material-ui/icons/DeleteForever";

class StudentRow extends Component {

    handleDeleteStudent = (event) => {
        event.preventDefault();

        //TODO: Add
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell component="th" scope="row">
                    {this.props.studentName}
                </CustomTableCell>
                <CustomTableCell>
                    <CustomTableCell>
                        <IconButton aria-label="Usuń" onClick={this.handleDeleteStudent}>
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
