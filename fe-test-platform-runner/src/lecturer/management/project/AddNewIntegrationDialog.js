import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import {styles} from "../../../styles/ProjectBoardStyles";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import * as Api from "../../../Api";
import FormControl from "@material-ui/core/FormControl/FormControl";
import InputLabel from "@material-ui/core/InputLabel/InputLabel";
import Select from "@material-ui/core/Select/Select";
import Input from "@material-ui/core/Input/Input";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import FormHelperText from "@material-ui/core/FormHelperText/FormHelperText";
import ArrowRightIcon from "@material-ui/icons/ArrowRightAlt";

class AddNewIntegrationDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {integrationName: null, selectedStages: ["Brak", "Brak", "Brak", "Brak", "Brak"]};
    }

    handleIntegrationNameAdded = () => event => {
        this.setState({integrationName: event.target.value})
    };

    handleAddNewIntegration = () => {

        var integrationStages = [];
        var i = 0;
        var j = 0;

        this.state.selectedStages.forEach(stage => {
                if (this.state.selectedStages[j] !== "Brak") {

                    var intStage = {};
                    intStage.name = this.state.integrationName + i;
                    intStage.orderNumber = i + 1;
                    intStage.stageName = this.state.selectedStages[j];
                    integrationStages[i] = intStage;
                    i++;
                }
                j++;
            }
        );

        const data = {};
        data.name = this.state.integrationName;
        data.integrationStages = integrationStages;

        console.log(JSON.stringify(data));

        Api.addNewIntegration(this.props.projectName, JSON.stringify(data))
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
    };

    handleStageSelected = (index) => event => {
        console.log(event.target.value);
        this.state.selectedStages[index] = event.target.value;
        this.forceUpdate()
    };

    render() {
        return (
            <Dialog open={this.props.isOpen}
                    onClose={this.props.closeActionHandler}
                    aria-labelledby="form-dialog-title"
                    fullWidth="true"
                    maxWidth="md">
                <DialogTitle id="form-dialog-title">Dodaj nową integrację</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Podaj informacje o integracji:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa integracji"
                        className={this.props.classes.textField}
                        value={this.state.integrationName}
                        onChange={this.handleIntegrationNameAdded()}
                        margin="normal"/>
                    <div display="flex" flexDirection="column" width="fit-content">
                        <FormControl>
                            <InputLabel htmlFor="age-helper">Etap</InputLabel>
                            <Select value={this.state.selectedStages[0]}
                                    onChange={this.handleStageSelected(0)}
                                    input={<Input name="age" id="age-helper"/>}>
                                <MenuItem value="Brak">
                                    <em>Brak</em>
                                </MenuItem>
                                {this.props.availableStages.map(stage =>
                                    <MenuItem value={stage}>{stage}</MenuItem>)
                                }
                            </Select>
                            <FormHelperText>Wybierz 1. moduł integracji</FormHelperText>
                        </FormControl>
                        <ArrowRightIcon/>
                        <FormControl>
                            <InputLabel htmlFor="age-helper">Etap</InputLabel>
                            <Select value={this.state.selectedStages[1]}
                                    onChange={this.handleStageSelected(1)}
                                    input={<Input name="age" id="age-helper"/>}>
                                <MenuItem value="Brak">
                                    <em>Brak</em>
                                </MenuItem>
                                {this.props.availableStages.map(stage =>
                                    <MenuItem value={stage}>{stage}</MenuItem>)
                                }
                            </Select>
                            <FormHelperText>Wybierz 2. moduł integracji</FormHelperText>
                        </FormControl>
                        <ArrowRightIcon/>
                        <FormControl>
                            <InputLabel htmlFor="age-helper">Etap</InputLabel>
                            <Select value={this.state.selectedStages[2]}
                                    onChange={this.handleStageSelected(2)}
                                    input={<Input name="age" id="age-helper"/>}>
                                <MenuItem value="Brak">
                                    <em>Brak</em>
                                </MenuItem>
                                {this.props.availableStages.map(stage =>
                                    <MenuItem value={stage}>{stage}</MenuItem>)
                                }
                            </Select>
                            <FormHelperText>Wybierz 3. moduł integracji</FormHelperText>
                        </FormControl>
                        <ArrowRightIcon/>
                        <FormControl>
                            <InputLabel htmlFor="age-helper">Etap</InputLabel>
                            <Select value={this.state.selectedStages[3]}
                                    onChange={this.handleStageSelected(3)}
                                    input={<Input name="age" id="age-helper"/>}>
                                <MenuItem value="Brak">
                                    <em>Brak</em>
                                </MenuItem>
                                {this.props.availableStages.map(stage =>
                                    <MenuItem value={stage}>{stage}</MenuItem>)
                                }
                            </Select>
                            <FormHelperText>Wybierz 4. moduł integracji</FormHelperText>
                        </FormControl>
                        <ArrowRightIcon/>
                        <FormControl>
                            <InputLabel htmlFor="age-helper">Etap</InputLabel>
                            <Select value={this.state.selectedStages[4]}
                                    onChange={this.handleStageSelected(4)}
                                    input={<Input name="age" id="age-helper"/>}>
                                <MenuItem value="Brak">
                                    <em>Brak</em>
                                </MenuItem>
                                {this.props.availableStages.map(stage =>
                                    <MenuItem value={stage}>{stage}</MenuItem>)
                                }
                            </Select>
                            <FormHelperText>Wybierz 5. moduł integracji</FormHelperText>
                        </FormControl>
                    </div>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleAddNewIntegration} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(AddNewIntegrationDialog);
