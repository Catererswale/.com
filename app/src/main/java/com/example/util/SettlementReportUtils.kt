package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.models.WeeklySettlementSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SettlementReportUtils {

    private const val TAG = "SettlementReportUtils"

    /**
     * Generates a PDF file on local storage containing the complete Weekly Settlement Breakdown for a kitchen.
     */
    fun generateSettlementPdf(context: Context, settlement: WeeklySettlementSummary): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint()
            val titlePaint = Paint()
            val boldPaint = Paint()

            // Header Background Accent (Saffron Orange)
            paint.color = Color.rgb(255, 109, 0)
            canvas.drawRect(0f, 0f, 595f, 90f, paint)

            // Header Text
            titlePaint.color = Color.WHITE
            titlePaint.textSize = 20f
            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("A1 HUMA CATERERS - WEEKLY SETTLEMENT", 30f, 42f, titlePaint)

            titlePaint.textSize = 12f
            titlePaint.typeface = Typeface.DEFAULT
            canvas.drawText("Partner Kitchen Settlement & Payout Advice", 30f, 65f, titlePaint)

            // General Info Section
            paint.color = Color.BLACK
            paint.textSize = 12f

            boldPaint.color = Color.BLACK
            boldPaint.textSize = 13f
            boldPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            var y = 130f

            canvas.drawText("Settlement Statement ID: ${settlement.settlementId}", 30f, y, boldPaint)
            y += 22f
            canvas.drawText("Kitchen Name: ${settlement.catererName}", 30f, y, paint)
            y += 20f
            canvas.drawText("Settlement Cycle (Mon-Sun): ${settlement.weekLabel}", 30f, y, paint)
            y += 20f
            
            val formattedDate = if (settlement.settledAtTimestamp != null) {
                SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(settlement.settledAtTimestamp))
            } else {
                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date())
            }
            canvas.drawText("Statement Date: $formattedDate", 30f, y, paint)
            y += 30f

            // Divider Line
            paint.color = Color.LTGRAY
            paint.strokeWidth = 1f
            canvas.drawLine(30f, y, 565f, y, paint)
            y += 25f

            // Table Header Background
            paint.color = Color.rgb(245, 245, 245)
            canvas.drawRect(30f, y, 565f, y + 28f, paint)

            boldPaint.color = Color.rgb(50, 50, 50)
            boldPaint.textSize = 11f
            canvas.drawText("FINANCIAL BREAKDOWN ITEM", 40f, y + 18f, boldPaint)
            canvas.drawText("AMOUNT (₹)", 450f, y + 18f, boldPaint)
            y += 40f

            // Table Rows
            fun drawTableRow(label: String, amountStr: String, isBold: Boolean = false, isHighlight: Boolean = false) {
                if (isHighlight) {
                    val bgPaint = Paint()
                    bgPaint.color = Color.rgb(232, 245, 233)
                    canvas.drawRect(30f, y - 14f, 565f, y + 12f, bgPaint)
                }

                val rowPaint = if (isBold) boldPaint else paint
                rowPaint.color = if (isHighlight) Color.rgb(46, 125, 50) else Color.BLACK
                rowPaint.textSize = 12f

                canvas.drawText(label, 40f, y, rowPaint)
                canvas.drawText(amountStr, 450f, y, rowPaint)

                y += 24f
            }

            drawTableRow("Total Completed Orders", "${settlement.totalOrdersCount} Orders (${settlement.onlineOrdersCount} Online, ${settlement.offlineOrdersCount} Offline)")
            drawTableRow("Gross Sales Revenue", "₹${String.format("%.2f", settlement.grossSalesAmount)}")
            drawTableRow("Online Platform Fee (${settlement.onlineCommissionPercentage}%)", "-₹${String.format("%.2f", settlement.onlineCommissionAmount)}")
            drawTableRow("Offline Platform Fee (${settlement.offlineCommissionPercentage}%)", "-₹${String.format("%.2f", settlement.offlineCommissionAmount)}")
            drawTableRow("Estimated GST (5%)", "₹${String.format("%.2f", settlement.totalGstAmount)}")
            drawTableRow("Online Advance (Held by Platform)", "₹${String.format("%.2f", settlement.advancePaidByCustomers)}")
            drawTableRow("70% Cash Collected by Delivery Boy (In Kitchen Hand)", "₹${String.format("%.2f", settlement.balanceCollectedAtDelivery)}")
            drawTableRow("Total Direct Cash in Kitchen Hand", "₹${String.format("%.2f", settlement.cashReceivedByKitchen)}")
            
            y += 10f
            canvas.drawLine(30f, y, 565f, y, paint)
            y += 20f

            drawTableRow("NET BANK PAYOUT FROM PLATFORM", "₹${String.format("%.2f", settlement.netPayableToKitchen)}", isBold = true, isHighlight = true)

            y += 30f
            // Bank Payout Details
            boldPaint.color = Color.BLACK
            boldPaint.textSize = 13f
            canvas.drawText("Bank Payout & Transfer Details", 30f, y, boldPaint)
            y += 22f

            paint.color = Color.DKGRAY
            paint.textSize = 11f
            canvas.drawText("Bank Account Number: ${settlement.payoutBankAcc.ifBlank { "Not configured" }}", 30f, y, paint)
            y += 18f
            canvas.drawText("Bank IFSC Code: ${settlement.payoutIfsc.ifBlank { "N/A" }}", 30f, y, paint)
            y += 18f
            canvas.drawText("Settlement Status: ${settlement.settlementStatus.name}", 30f, y, paint)
            y += 18f
            canvas.drawText("Bank UTR / Ref Number: ${settlement.utrTransactionNumber ?: "Pending Transfer"}", 30f, y, paint)

            // Footer
            y = 800f
            paint.color = Color.GRAY
            paint.textSize = 10f
            canvas.drawText("Automated Settlement Statement generated by A1 Huma Caterers Super Admin Platform.", 30f, y, paint)

            pdfDocument.finishPage(page)

            val dir = File(context.cacheDir, "settlements")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "Settlement_${settlement.catererName.replace(" ", "_")}_${settlement.weekStartDate}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()

            Log.d(TAG, "Generated settlement PDF at ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error generating settlement PDF: ${e.message}", e)
            null
        }
    }

    /**
     * Builds structured Email or WhatsApp text message summarizing the settlement.
     */
    fun buildSettlementSummaryText(settlement: WeeklySettlementSummary): String {
        return """
            📊 *WEEKLY SETTLEMENT STATEMENT*
            *A1 Huma Caterers Partner Kitchen Platform*
            ------------------------------------------
            🏢 *Kitchen Name:* ${settlement.catererName}
            📅 *Cycle Period:* ${settlement.weekLabel}
            🆔 *Statement ID:* ${settlement.settlementId}

            💰 *Sales & Orders Breakdown:*
            • Total Completed Orders: ${settlement.totalOrdersCount} (${settlement.onlineOrdersCount} Online, ${settlement.offlineOrdersCount} Offline)
            • Gross Sales: ₹${settlement.grossSalesAmount.toInt()}
            • Online Platform Fee (${settlement.onlineCommissionPercentage}%): -₹${settlement.onlineCommissionAmount.toInt()}
            • Offline Platform Fee (${settlement.offlineCommissionPercentage}%): -₹${settlement.offlineCommissionAmount.toInt()}
            • Total Platform Commission: -₹${settlement.adminCommissionAmount.toInt()}
            • Platform Online Advances: ₹${settlement.advancePaidByCustomers.toInt()}
            • 70% Balance Cash Collected by Delivery Boy: ₹${settlement.balanceCollectedAtDelivery.toInt()}
            • Total Direct Cash in Kitchen Hand: ₹${settlement.cashReceivedByKitchen.toInt()}

            ✅ *NET PLATFORM BANK PAYOUT:* ₹${settlement.netPayableToKitchen.toInt()}
            ------------------------------------------
            🏦 *Bank Account:* ${settlement.payoutBankAcc} (${settlement.payoutIfsc})
            📑 *UTR Reference:* ${settlement.utrTransactionNumber ?: "Pending"}
            📌 *Status:* ${settlement.settlementStatus.name}

            Thank you for partnering with A1 Huma Caterers!
        """.trimIndent()
    }

    /**
     * Triggers Android share / Email intent to send the settlement report PDF and breakdown text to kitchen owner.
     */
    fun sendSettlementEmailOrShare(context: Context, settlement: WeeklySettlementSummary, pdfFile: File?) {
        try {
            val summaryText = buildSettlementSummaryText(settlement)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "Weekly Settlement Statement - ${settlement.catererName} (${settlement.weekLabel})")
                putExtra(Intent.EXTRA_TEXT, summaryText)

                if (pdfFile != null && pdfFile.exists()) {
                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        pdfFile
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            val chooser = Intent.createChooser(intent, "Send Settlement Report via Email / WhatsApp")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing settlement statement: ${e.message}")
        }
    }
}
