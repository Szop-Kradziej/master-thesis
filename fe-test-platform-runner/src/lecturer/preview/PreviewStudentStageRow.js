import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../../backendUrl";
import Typography from '@material-ui/core/Typography';
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import TableCell from "@material-ui/core/TableCell/TableCell";
import EditItemComponent from "../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../Api";
import StudentUploadBinaryDialog from "./PreviewStudentUploadBinaryDialog";
import StudentUploadReportDialog from "./PreviewStudentUploadReportDialog";
import StudentUploadCodeLinkDialog from "./PreviewStudentUploadCodeLinkDialog";
import {getAuthHeader} from "../../Api";
import DescriptionStatisticsAndCommentItemComponent from "../../utils/DescriptionStatisticsAndCommentItemComponent";
import UploadDownloadAndRunItemComponent from "../../utils/UploadDownloadAndRunItemComponent";

class PreviewStudentStageRow extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddBinaryDialogVisible: false,
            inputBinaryFile: null,
            isAddReportDialogVisible: false,
            inputReportFile: null,
            stage: null,
            isAddCodeLinkDialogVisible: false,
            codeLink: null
        };
    }

    handleOpenAddBinaryDialog = () => {
        this.setState({isAddBinaryDialogVisible: true});
    };

    handleCloseAddBinaryDialog = () => {
        this.setState({isAddBinaryDialogVisible: false});
    };

    handleRunTests = () => {
        return fetch(backendUrl(`/preview/${this.props.groupName}/stage/run`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Authorization': getAuthHeader()},
            body: "projectName=" + this.props.projectName + "&stageName=" + this.props.stage.stageName
        })
            .then(this.props.stageChangedHandler)
    };

    handleOpenAddReportDialog = () => {
        this.setState({isAddReportDialogVisible: true});
    };

    handleCloseAddReportDialog = () => {
        this.setState({isAddReportDialogVisible: false});
    };

    handleOpenAddCodeLinkDialog = () => {
        this.setState({isAddCodeLinkDialogVisible: true});
    };

    handleCloseAddCodeLinkDialog = () => {
        this.setState({isAddCodeLinkDialogVisible: false});
    };

    getCodeLink = () => {
        return (
            <InputWrapper>
                <a target='_blank' href={this.props.stage.codeLink}>
                    Przejdź do kodu
                </a>
            </InputWrapper>
        )
    };

    handleDownloadReport = () => {
        Api.downloadPreviewReport(this.props.groupName, this.props.projectName, this.props.stage.stageName)
    };

    handleDownloadBin = () => {
        Api.downloadPreviewBin(this.props.groupName, this.props.projectName, this.props.stage.stageName)
    };

    handleDownloadStageDescription = () => {
        Api.downloadStageDescription(this.props.projectName, this.props.stage.stageName)
    };

    handleDownloadStageStatistics = () => {
        Api.downloadStageStatistics(this.props.groupName, this.props.projectName, this.props.stage.stageName)
    };

    render() {
        return (
            <div className={this.props.classes.stageRow}>
                <Typography className={this.props.classes.heading}>
                    <DescriptionStatisticsAndCommentItemComponent
                        header={this.props.stage.stageName}
                        descriptionInfo="Pobierz opis etapu"
                        getDescriptionActionHandler={this.handleDownloadStageDescription}
                        statisticsInfo="Pobierz statystyki dla etapu"
                        statisticsDisabled={this.props.stage.statistics === false}
                        getStatisticsActionHandler={this.handleDownloadStageStatistics}
                        commentInfo={this.props.stage.comment ? this.props.stage.comment : 'Brak'}
                        commentDisabled={this.props.stage.comment == null}/>
                </Typography>
                <div className={this.props.classes.inputWrapper}>
                    <Table width="1700">
                        <TableHead>
                            <TableRow>
                                <CustomTableCell>
                                    <UploadDownloadAndRunItemComponent
                                        header="Bin:"
                                        uploadInfo="Załaduj plik binarny"
                                        uploadActionHandler={this.handleOpenAddBinaryDialog}
                                        downloadInfo="Pobierz plik binarny"
                                        disabled={this.props.stage.binaryName === null}
                                        downloadActionHandler={this.handleDownloadBin}
                                        runInfo="Uruchom plik binarny"
                                        runActionHandler={this.handleRunTests}/>
                                </CustomTableCell>
                                <CustomTableCell>
                                    <EditItemComponent
                                        header="Kod:"
                                        info="Edytuj link do kodu"
                                        editActionHandler={this.handleOpenAddCodeLinkDialog}/>
                                </CustomTableCell>
                                <CustomTableCell>
                                    <UploadAndDownloadItemComponent
                                        header="Raport:"
                                        uploadInfo="Załaduj raport"
                                        uploadActionHandler={this.handleOpenAddReportDialog}
                                        downloadInfo="Pobierz raport"
                                        downloadDisabled={this.props.stage.reportName === null}
                                        downloadActionHandler={this.handleDownloadReport}/>
                                </CustomTableCell>
                                <CustomTableCell/>
                                <CustomTableCell>Zaliczone:</CustomTableCell>
                                <CustomTableCell>Grupy:</CustomTableCell>
                                <CustomTableCell>Rozpoczęcie:</CustomTableCell>
                                <CustomTableCell>Zakończenie:</CustomTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <TableRow key="custom_key">
                                <CustomTableCell component="th" scope="row" width="15%">
                                    <div display="flex">
                                        {/*TODO: It seems that it is incorrect approach to use props instead of state here, check: https://github.com/uberVU/react-guide/issues/17*/}
                                        <p> {this.props.stage.binaryName ? this.props.stage.binaryName : 'Brak pliku'} </p>
                                    </div>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="15%">
                                    <p> {this.props.stage.codeLink ? this.getCodeLink() : 'Brak linku'}</p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="15%">
                                    <p> {this.props.stage.reportName ? this.props.stage.reportName : 'Brak pliku'} </p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row"/>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p> {this.props.stage.passedTestCasesCount}/{this.props.stage.allTestCasesCount}</p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p> {this.props.stage.successfulGroups}/{this.props.stage.totalGroupsNumber}</p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p>{this.props.stage.startDate ? this.props.stage.startDate : 'Brak'}</p>
                                </CustomTableCell>
                                <CustomTableCell component="th" scope="row" width="5%">
                                    <p>{this.props.stage.endDate ? this.props.stage.endDate : 'Brak'}</p>
                                </CustomTableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
                <InputWrapper>
                    <StudentUploadBinaryDialog isOpen={this.state.isAddBinaryDialogVisible}
                                               closeActionHandler={this.handleCloseAddBinaryDialog}
                                               successActionHandler={this.props.stageChangedHandler}
                                               groupName={this.props.groupName}
                                               projectName={this.props.projectName}
                                               stageName={this.props.stage.stageName}
                                               headerText="Dodaj binarkę"/>
                    <StudentUploadReportDialog isOpen={this.state.isAddReportDialogVisible}
                                               closeActionHandler={this.handleCloseAddReportDialog}
                                               successActionHandler={this.props.stageChangedHandler}
                                               groupName={this.props.groupName}
                                               projectName={this.props.projectName}
                                               stageName={this.props.stage.stageName}
                                               headerText="Dodaj raport"/>
                    <StudentUploadCodeLinkDialog isOpen={this.state.isAddCodeLinkDialogVisible}
                                                 closeActionHandler={this.handleCloseAddCodeLinkDialog}
                                                 successActionHandler={this.props.stageChangedHandler}
                                                 groupName={this.props.groupName}
                                                 projectName={this.props.projectName}
                                                 stageName={this.props.stage.stageName}
                                                 headerText="Podaj adres kodu"/>
                </InputWrapper>
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

export default withStyles(styles)(PreviewStudentStageRow);
