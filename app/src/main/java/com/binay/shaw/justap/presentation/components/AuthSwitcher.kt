package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * Composable function that displays a text with a clickable portion.

 * @param text The main text to display.
 * @param clickableText The clickable portion of the text.
 * @param modifier The modifier to be applied to the ClickableText (Optional).
 * @param textStyle The style to be applied to the main text (Optional).
 * @param clickableTextStyle The style to be applied to the clickable portion of the text (Optional).
 * @param onClick The callback to be invoked when the clickable portion of the text is clicked.
 */
@Composable
fun AuthSwitcher(
    text: String,
    clickableText: String,
    modifier: Modifier = Modifier,
    textStyle: SpanStyle = SpanStyle(
        color = Color.Black,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    clickableTextStyle: SpanStyle = SpanStyle(
        color = Color.Blue,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    onClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = textStyle
        ) {
            append(text)
        }
        append(" ")
        pushStringAnnotation(tag = clickableText, annotation = clickableText)
        withStyle(
            style = clickableTextStyle
        ) {
            append(clickableText)
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.fillMaxWidth().then(modifier),
        style = TextStyle(
            textAlign = TextAlign.Center
        ),
        text = annotatedString,
        onClick = { offset ->
            val annotations = annotatedString.getStringAnnotations(
                tag = clickableText,
                start = offset,
                end = offset
            )
            if (annotations.isNotEmpty()) {
                onClick()
            }
        }
    )
}