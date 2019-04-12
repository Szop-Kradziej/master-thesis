import {withStyles} from "@material-ui/core";
import TableCell from "@material-ui/core/TableCell/TableCell";

export const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 16
    },
    body: {
        color: "black",
        fontSize: 16
    }
}))(TableCell);

export const styles = (theme) => ({
    app: {
        textAlign: "center",
        fontSize: 24,
        fontWeight: 700
    },
    table: {
        minWidth: 900,
        fontSize: 16,
        color: "black"
    },
    button: {
        backgroundColor: "#5aa724",
        color: "black",
        marginTop: 20
    },
    link: {
        textDecoration: "none"
    },
    paper: {
        position: 'absolute',
        width: theme.spacing.unit * 50,
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[5],
        padding: theme.spacing.unit * 4,
        outline: 'none',
    },
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 200,
    },
    testCase: {
        marginLeft: 100,
        backgroundColor: "#e0e0e0",
    },
    root: {
        width: '100%',
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        fontWeight: theme.typography.fontWeightRegular,
    },
});