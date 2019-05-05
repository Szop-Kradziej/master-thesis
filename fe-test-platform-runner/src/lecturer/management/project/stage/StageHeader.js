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
import UploadAndDownloadItemComponent from "../../../../utils/UploadAndDownloadItemComponent";

class StageHeader extends Component {

    handleEditStageName = () => {
        //TODO: do action
    };

    handleEditStageDeadline = () => {
        //TODO: do action
    };

    handleUploadStageDescription = () => {
        //TODO: do action
    };

    handleDownloadStageDescription = () => {
        //TODO: do action
    };

    render() {
        return (
            <Table>
                <TableHead>
                    <TableRow>
                        <CustomTableCell width="15%">
                            <EditItemComponent
                                header="Nazwa etapu:"
                                info="Edytuj nazwę etapu"
                                editActionHandler={this.handleEditStageName}/>
                        </CustomTableCell>
                        <CustomTableCell width="15%">
                            <UploadAndDownloadItemComponent
                                header="Opis etapu:"
                                uploadInfo="Załaduj opis etapu"
                                uploadActionHandler={this.handleUploadStageDescription}
                                downloadInfo="Pobierz opis etapu"
                                downloadActionHandler={this.handleDownloadStageDescription}/>
                        </CustomTableCell>
                        <CustomTableCell width="50%"/>
                        <CustomTableCell width="15%">
                            <EditItemComponent
                                header="Deadline:"
                                info="Edytuj deadline etapu"
                                editActionHandler={this.handleEditStageDeadline}/>
                        </CustomTableCell>
                        <CustomTableCell>
                            <IconButton aria-label="Usuń">
                                <DeleteIcon/>
                            </IconButton>
                        </CustomTableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <CustomTableCell component="th" scope="row">
                        {this.props.stageName}
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row">
                        Description.pdf
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row"/>
                    <CustomTableCell component="th" scope="row">
                        10/06/2019 23:59
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row"/>
                </TableBody>
            </Table>
        );
    }
}

export default withStyles(styles)(StageHeader);
