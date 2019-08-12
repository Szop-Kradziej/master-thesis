import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../../../../styles/ProjectBoardStyles";
import EditItemComponent from "../../../../utils/EditItemComponent";
import * as Api from "../../../../Api";
import DeleteItemComponent from "../../../../utils/DeleteItemComponent";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import CommentIcon from "@material-ui/icons/Info";
import IconButton from "@material-ui/core/es/IconButton/IconButton";
import InputWrapper from "../../../../utils/InputWrapper";

class GroupHeader extends Component {

    handleEditIntegrationName = () => {
        //TODO: do action
    };

    handleEditIntegrationSchema = () => {
        //TODO: do action
    };

    handleOpenEditCommentDialog = () => {
        //TODO: implement
    };

    handleDeleteIntegration = () => {
        Api.deleteIntegration(this.props.projectName, this.props.integration.name)
            .then(this.props.integrationChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    //TODO: Move to separate function - remove duplicates
    createIntegrationSchema = () => {
        var i = 1;
        var text = "";
        this.props.integration.integrationStages.map(integrationStage => {
            text = text + integrationStage.orderNumber + ". " + integrationStage.stageName;

            if (i < this.props.integration.integrationStages.length) {
                text = text + " -> ";
            }
            i++;
        });

        return (<div>{text ? text : "Brak"}</div>);
    };

    render() {
        return (
            <Table>
                <TableHead>
                    <TableRow>
                        <CustomTableCell width="15%">
                            <EditItemComponent
                                header="Nazwa integracji:"
                                info="Edytuj nazwę integracji"
                                editActionHandler={this.handleEditIntegrationName}/>
                        </CustomTableCell>
                        <CustomTableCell width="65%">
                            <EditItemComponent
                                header="Schemat integracji:"
                                info="Edytuj schemat integracji"
                                editActionHandler={this.handleEditIntegrationSchema}/>
                        </CustomTableCell>
                        <CustomTableCell width="15%">
                            <EditItemComponent
                                header="Komentarz:"
                                info="Edytuj komentarz dla etapu"
                                editActionHandler={this.handleOpenEditCommentDialog}/>
                        </CustomTableCell>
                        <CustomTableCell>
                            <DeleteItemComponent info="Usuń proces integracji"
                                                 deleteActionHandler={this.handleDeleteIntegration}/>
                        </CustomTableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <CustomTableCell component="th" scope="row">
                        {this.props.integration.name}
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row">
                        {this.createIntegrationSchema()}
                    </CustomTableCell>
                    <CustomTableCell>
                        {this.props.integration.comment == null ? 'Brak' :
                            <InputWrapper>
                                <IconButton>
                                    <Tooltip
                                        title={this.props.integration.comment}>
                                        <CommentIcon/>
                                    </Tooltip>
                                </IconButton>
                            </InputWrapper>}
                    </CustomTableCell>
                    <CustomTableCell component="th" scope="row"/>
                </TableBody>
            </Table>
        );
    }
}

export default withStyles(styles)(GroupHeader);
