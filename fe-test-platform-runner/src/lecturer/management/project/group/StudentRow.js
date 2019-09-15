import React, {Component} from "react";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import TableRow from "@material-ui/core/TableRow/TableRow";
import * as Api from "../../../../Api";
import DeleteItemComponent from "../../../../utils/DeleteItemComponent";

class StudentRow extends Component {

    handleRemoveStudent = (event) => {
        event.preventDefault();

        Api.removeStudentFromGroup(this.props.projectName, this.props.groupName, this.props.studentName)
            .then(this.props.groupChangedHandler)
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell>
                    {this.props.studentName}
                </CustomTableCell>
                <CustomTableCell>
                    <DeleteItemComponent info="Usuń studenta z grupy"
                                         deleteActionHandler={this.handleRemoveStudent}/>
                </CustomTableCell>
                <CustomTableCell/>
            </TableRow>
        );
    }
}

export default (StudentRow);
