import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../../backendUrl";
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import RunIcon from '@material-ui/icons/PlayArrow';
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import TableCell from "@material-ui/core/TableCell/TableCell";
import {getAdminAuthHeader} from "../../Api";
import AssessmentIcon from "@material-ui/icons/Assessment";
import * as Api from "../../Api";

class PreviewStudentIntegrationRow extends Component {

    handleRunTests = () => {
        fetch(backendUrl(`/preview/${this.props.groupName}/integration/run`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': getAdminAuthHeader()},
            body: "projectName=" + this.props.projectName + "&integrationName=" + this.props.integration.integrationName
        })
            .then(this.props.integrationChangedHandler)
            .then(function (response) {
            })
    };

    handleDownloadIntegrationStatistics = () => {
        Api.downloadIntegrationStatistics(this.props.groupName, this.props.projectName, this.props.integration.integrationName)
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
            <div className={this.props.classes.stageRow}>
                <Typography className={this.props.classes.heading}>
                    <InputWrapper>
                        {this.props.integration.integrationName}
                        <IconButton aria-label="Pobierz statystyki dla integracji"
                                    disabled={this.props.integration.statistics === false}
                                    onClick={this.handleDownloadIntegrationStatistics}>
                            <AssessmentIcon/>
                        </IconButton>
                    </InputWrapper>
                </Typography>
                <div className={this.props.classes.inputWrapper}>
                    <Table width="1700">
                        <TableHead>
                            <TableRow>
                                <CustomTableCell>
                                    {/*TODO: Remove tags from wrapper*/}
                                    <InputWrapper>
                                        Uruchom integrację
                                        <IconButton aria-label="Uruchom"
                                                    disabled={this.props.integration.enable === false}
                                                    onClick={this.handleRunTests}>
                                            <RunIcon/>
                                        </IconButton>
                                    </InputWrapper>
                                </CustomTableCell>
                                <CustomTableCell>Schemat integracji:</CustomTableCell>
                                <CustomTableCell>Zaliczone:</CustomTableCell>
                                <CustomTableCell>Grupy:</CustomTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <TableRow key="custom_key">
                                <CustomTableCell component="th" scope="row" width="15%">
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row">
                                    {this.createIntegrationSchema()}
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p> {this.props.integration.passedTestCasesCount}/{this.props.integration.allTestCasesCount}</p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p> {this.props.integration.successfulGroups}/{this.props.integration.totalGroupsNumber}</p>
                                </CustomTableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
            </div>
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

const stopPropagation = (e) => e.stopPropagation();
const InputWrapper = ({children}) =>
    <div onClick={stopPropagation}>
        {children}
    </div>;

export const styles = (theme) => ({
    button: {
        backgroundColor: "#5aa724",
        color: "black",
        marginTop: 0
    },
    stageRow: {
        display: 'block',
        width: 1700
    },
    heading: {
        fontSize: 20,
        fontWeight: 700
    },
    inputWrapper: {
        display: 'flex',
        width: 1700
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 200,
    },
});

export default withStyles(styles)(PreviewStudentIntegrationRow);
