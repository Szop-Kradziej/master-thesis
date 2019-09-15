import Button from "@material-ui/core/Button/Button";
import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../styles/ProjectBoardStyles";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import TextField from "@material-ui/core/TextField/TextField";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import Table from "@material-ui/core/Table/Table";
import TableBody from "@material-ui/core/es/TableBody/TableBody";
import AddNewItemComponent from "../../../../utils/AddNewItemComponent";
import StudentRow from "./StudentRow";
import * as Api from "../../../../Api";

class StudentsDetails extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isNewStudentDialogVisible: false,
            newStudentName: null,
            inputFile: null,
            outputFile: null
        };
    }

    handleOpenNewStudentDialog = () => {
        this.setState({isNewStudentDialogVisible: true});
    };

    handleCloseNewStudentDialog = () => {
        this.setState({isNewStudentDialogVisible: false});
    };

    handleAddNewStudentName = name => event => {
        this.setState({[name]: event.target.value});
    };

    handleAddStudent = () => {
        Api.addNewStudent(this.props.projectName, this.props.groupName, this.state.newStudentName)
            .then(this.handleCloseNewStudentDialog)
            .then(this.props.groupChangedHandler)
    };

    render() {
        return (
            <div className={this.props.classes.panel}>
                <div className={this.props.classes.testLabel}>
                <AddNewItemComponent
                    header="Studenci"
                    info="Dodaj nowego studenta"
                    addActionHandler={this.handleOpenNewStudentDialog}/>
                </div>
                {this.isAnyStudentExist() ? this.renderStudentsTable() : this.renderNoStudentsLabel()}
                <Dialog open={this.state.isNewStudentDialogVisible} onClose={this.handleCloseNewStudentDialog}
                        aria-labelledby="form-dialog-title">
                    <DialogTitle id="form-dialog-title">Dodaj nowego studenta</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Podaj nazwę studenta:
                        </DialogContentText>
                        <TextField
                            id="standard-name"
                            label="Nazwa studenta"
                            className={this.props.classes.textField}
                            value={this.state.newStudentName}
                            onChange={this.handleAddNewStudentName("newStudentName")}
                            margin="normal"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseNewStudentDialog} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleAddStudent} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }

    isAnyStudentExist() {
        return this.props.students && this.props.students.length > 0;
    }

    renderStudentsTable() {
        return (
            <Table display={this.props.students}>
                <TableHead>
                    <TableRow>
                        <CustomTableCell width="50%">
                            Nazwa studenta:
                        </CustomTableCell>
                        <CustomTableCell width="5%"/>
                        <CustomTableCell/>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {this.props.students.map(student => (
                            <StudentRow
                                studentName={student}
                                projectName={this.props.projectName}
                                groupName={this.props.groupName}
                                groupChangedHandler={this.props.groupChangedHandler}/>
                        )
                    )}
                </TableBody>
            </Table>
        );
    }

    renderNoStudentsLabel() {
        return (
            <p className={this.props.classes.noTestText}>
                Brak przypisanych studentów
            </p>
        );
    }
}

export default withStyles(styles)(StudentsDetails);
