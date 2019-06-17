import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../../../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import EditItemComponent from "../../../../utils/EditItemComponent";
import * as Api from "../../../../Api";

class GroupHeader extends Component {

    handleEditGroupName = () => {
        //TODO: do action
    };

    handleDeleteGroup = () => {
        Api.deleteGroup(this.props.projectName, this.props.groupName)
            .then(this.props.groupChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <Table>
                <TableHead>
                    <TableRow>
                        <CustomTableCell width="15%">
                            <EditItemComponent
                                header="Nazwa grupy:"
                                info="Edytuj nazwę grupy"
                                editActionHandler={this.handleEditGroupName}/>
                        </CustomTableCell>
                        <CustomTableCell width="80%"/>
                        <CustomTableCell>
                            <IconButton aria-label="Usuń" onClick={this.handleDeleteGroup}>
                                <DeleteIcon/>
                            </IconButton>
                        </CustomTableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <CustomTableCell component="th" scope="row">
                        {this.props.groupName}
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row"/>
                </TableBody>
            </Table>
        );
    }
}

export default withStyles(styles)(GroupHeader);
