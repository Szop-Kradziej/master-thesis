import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../backendUrl";
import Typography from '@material-ui/core/Typography';
import Button from "@material-ui/core/Button/Button";
import IconButton from '@material-ui/core/IconButton';
import DownloadIcon from '@material-ui/icons/CloudDownload';
import UploadIcon from '@material-ui/icons/CloudUpload';
import RunIcon from '@material-ui/icons/PlayArrow';
import DescriptionIcon from '@material-ui/icons/Description';
import axios from "axios";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import TableCell from "@material-ui/core/TableCell/TableCell";
import TextField from "@material-ui/core/TextField/TextField";
import EditItemComponent from "../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../utils/UploadAndDownloadItemComponent";
import * as Api from "../Api";

class StudentStageRow extends Component {

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

    handleAddBinary = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputBinaryFile.files[0]);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stage.stageName);

        axios.post(backendUrl("/upload/bin"), data)
            .then(function (response) {
                console.log("success");
            })
            // .then(this.handleBinaryFileSaved)
            .then(this.handleCloseAddBinaryDialog)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleRunTests = () => {
        fetch(backendUrl(`/run`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: "projectName=" + this.props.projectName + "&stageName=" + this.props.stage.stageName
        })
            .then(this.props.stageChangedHandler)
            .then(function (response) {
            })
    };


    handleOpenAddReportDialog = () => {
        this.setState({isAddReportDialogVisible: true});
    };

    handleCloseAddReportDialog = () => {
        this.setState({isAddReportDialogVisible: false});
    };

    handleAddReport = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputReportFile.files[0]);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stage.stageName);

        axios.post(backendUrl("/upload/report"), data)
            .then(function (response) {
                console.log("success");
            })
            // .then(this.handleReportFileSaved)
            .then(this.handleCloseAddReportDialog)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleOpenAddCodeLinkDialog = () => {
        this.setState({isAddCodeLinkDialogVisible: true});
    };

    handleCloseAddCodeLinkDialog = () => {
        this.setState({isAddCodeLinkDialogVisible: false});
    };

    handleAddCodeLink = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('codeLink', this.state.codeLink);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stage.stageName);

        axios.post(backendUrl("/upload/code"), data)
            .then(function (response) {
                console.log("success");
            })
            // .then(this.handleReportFileSaved)
            .then(this.handleCloseAddCodeLinkDialog)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleCodeLinkAdded = () => event => {
        this.setState({codeLink: event.target.value})
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
        Api.downloadReport(this.props.projectName, this.props.stage.stageName)
    };

    handleDownloadBin = () => {
        Api.downloadBin(this.props.projectName, this.props.stage.stageName)
    };

    handleDownloadStageDescription = () => {
        Api.downloadStageDescription(this.props.projectName, this.props.stage.stageName)
    };

    render() {
        return (
            <div className={this.props.classes.stageRow}>
                <Typography className={this.props.classes.heading}>
                    <InputWrapper>
                        {this.props.stage.stageName}
                        <IconButton aria-label="Pobierz opis etapu" onClick={this.handleDownloadStageDescription}>
                            <DescriptionIcon/>
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
                                        Bin:
                                        <IconButton aria-label="Zmień" onClick={this.handleOpenAddBinaryDialog}>
                                            <UploadIcon/>
                                        </IconButton>
                                        <IconButton aria-label="Pobierz" onClick={this.handleDownloadBin}>
                                            <DownloadIcon/>
                                        </IconButton>
                                        <IconButton aria-label="Uruchom"
                                                    disabled={this.props.stage.binaryName === null}
                                                    onClick={this.handleRunTests}>
                                            <RunIcon/>
                                        </IconButton>
                                    </InputWrapper>
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
                                        downloadActionHandler={this.handleDownloadReport}/>
                                </CustomTableCell>
                                <CustomTableCell/>
                                <CustomTableCell>Zaliczone:</CustomTableCell>
                                <CustomTableCell>Deadline:</CustomTableCell>
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
                                <CustomTableCell component="th" scope="row" width="10%">
                                    <p>{this.props.stage.deadline}</p>
                                </CustomTableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </div>
                <InputWrapper>
                    <Dialog open={this.state.isAddBinaryDialogVisible} onClose={this.handleCloseAddBinaryDialog}
                            aria-labelledby="form-dialog-title">
                        <DialogTitle id="form-dialog-title">Dodaj binarkę</DialogTitle>
                        <DialogContent>
                            <DialogContentText>
                                Podaj ścieżkę pliku:
                            </DialogContentText>
                            {/*TODO: FIX: https://stackoverflow.com/questions/40589302/how-to-enable-file-upload-on-reacts-material-ui-simple-input*/}
                            <div className="form-group">
                                <input className="form-control" ref={(ref) => {
                                    this.inputBinaryFile = ref;
                                }} type="file"/>
                            </div>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleCloseAddBinaryDialog} color="primary">
                                Anuluj
                            </Button>
                            <Button onClick={this.handleAddBinary} color="primary">
                                Dodaj
                            </Button>
                        </DialogActions>
                    </Dialog>
                    <Dialog open={this.state.isAddReportDialogVisible} onClose={this.handleCloseAddReportDialog}
                            aria-labelledby="form-dialog-title">
                        <DialogTitle id="form-dialog-title">Dodaj raport</DialogTitle>
                        <DialogContent>
                            <DialogContentText>
                                Podaj ścieżkę pliku:
                            </DialogContentText>
                            {/*TODO: FIX: https://stackoverflow.com/questions/40589302/how-to-enable-file-upload-on-reacts-material-ui-simple-input*/}
                            <div className="form-group">
                                <input className="form-control" ref={(ref) => {
                                    this.inputReportFile = ref;
                                }} type="file"/>
                            </div>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleCloseAddReportDialog} color="primary">
                                Anuluj
                            </Button>
                            <Button onClick={this.handleAddReport} color="primary">
                                Dodaj
                            </Button>
                        </DialogActions>
                    </Dialog>
                    <Dialog open={this.state.isAddCodeLinkDialogVisible} onClose={this.handleCloseAddCodeLinkDialog}
                            aria-labelledby="form-dialog-title">
                        <DialogTitle id="form-dialog-title">Dodaj raport</DialogTitle>
                        <DialogContent>
                            <DialogContentText>
                                Podaj adres kodu:
                            </DialogContentText>
                            <TextField
                                id="standard-name"
                                label="Adres kodu"
                                className={this.props.classes.textField}
                                value={this.state.codeLink}
                                onChange={this.handleCodeLinkAdded()}
                                margin="normal"
                            />
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleCloseAddCodeLinkDialog} color="primary">
                                Anuluj
                            </Button>
                            <Button onClick={this.handleAddCodeLink} color="primary">
                                Dodaj
                            </Button>
                        </DialogActions>
                    </Dialog>
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

export default withStyles(styles)(StudentStageRow);
