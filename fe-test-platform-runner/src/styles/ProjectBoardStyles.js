import {withStyles} from "@material-ui/core";
import TableCell from "@material-ui/core/TableCell/TableCell";

export const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 12,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 12,
        border: 0
    }
}))(TableCell);

export const CustomHeaderCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 20,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 12,
        border: 0
    }
}))(TableCell);

export const styles = (theme) => ({
    app: {
        textAlign: "center",
        fontSize: 24,
        fontWeight: 700
    },
    table: {
        fontSize: 16,
        color: "black"
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
    testCasesRow: {
        display: 'flex'
    },
    root: {
        width: '100%',
    },
    heading: {
        width: 1700,
    },
    headingMainPanel: {
        fontSize: 20,
        fontWeight: 700,
    },
    testsHeading: {
        width: 1700,
        fontSize: 16,
        fontWeight: 700
    },
});