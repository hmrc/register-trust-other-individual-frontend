/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.answers

import base.SpecBase
import controllers.register.individual.{routes => rts}
import models.{FullName, InternationalAddress, UkAddress, UserAnswers, YesNoDontKnow}
import pages.register.individual._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class OtherIndividualAnswersHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: FullName = FullName("First", None, "Last")
  private val arg = name.toString
  private val ukAddress: UkAddress = UkAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val nonUkCountry: String = "FR"
  private val canEdit: Boolean = true
  private val dateOfBirth: LocalDate = LocalDate.parse("1960-02-16")
  private val nino: String = "AA000000A"

  "other individual answers helper" when {

    "other individual has mentalCapacity No" in {
      val userAnswers: UserAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value
        .set(MentalCapacityYesNoPage(index), YesNoDontKnow.No).success.value

      val helper = injector.instanceOf[OtherIndividualAnswersHelper]

      val result = helper.otherIndividuals(userAnswers)

      result mustBe
        Some(Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.otherIndividual.subheading"),
            Seq(
              AnswerRow("otherIndividual.name.checkYourAnswersLabel", Html(name.displayFullName), Some(rts.NameController.onPageLoad(index, fakeDraftId).url), "", canEdit),
              AnswerRow("otherIndividual.5mld.mentalCapacityYesNo.checkYourAnswersLabel", Html("No"), Some(rts.MentalCapacityYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit)
            ),
            sectionKey = None,
            headingArgs = Seq(index + 1)
          )
        ))
    }

    "other individual has mentalCapacity Don't know" in {
      val userAnswers: UserAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value
        .set(MentalCapacityYesNoPage(index), YesNoDontKnow.DontKnow).success.value

      val helper = injector.instanceOf[OtherIndividualAnswersHelper]
      val result = helper.otherIndividuals(userAnswers)

      result mustBe
        Some(Seq(
          AnswerSection(
            headingKey = Some("answerPage.section.otherIndividual.subheading"),
            Seq(
              AnswerRow("otherIndividual.name.checkYourAnswersLabel", Html(name.displayFullName), Some(rts.NameController.onPageLoad(index, fakeDraftId).url), "", canEdit),
              AnswerRow("otherIndividual.5mld.mentalCapacityYesNo.checkYourAnswersLabel", Html("I don’t know"), Some(rts.MentalCapacityYesNoController.onPageLoad(index, fakeDraftId).url), arg, canEdit)
            ),
            sectionKey = None,
            headingArgs = Seq(index + 1)
          )
        ))
    }

    "return a other individual answer section" when {

        "minimum data" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).success.value
            .set(DateOfBirthYesNoPage(index), false).success.value
            .set(CountryOfNationalityYesNoPage(index), false).success.value
            .set(NationalInsuranceYesNoPage(index), true).success.value
            .set(NationalInsuranceNumberPage(index), nino).success.value
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          val helper = injector.instanceOf[OtherIndividualAnswersHelper]

          val result: Option[Seq[AnswerSection]] = helper.otherIndividuals(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.otherIndividual.subheading"),
              rows = Seq(
                AnswerRow(label = "otherIndividual.name.checkYourAnswersLabel", answer = Html(name.toString), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "otherIndividual.dateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.nationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.NationalInsuranceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.nationalInsuranceNumber.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(rts.NationalInsuranceNumberController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString)
              ),
              sectionKey = None,
              headingArgs = Seq(index + 1)
            )
          ))
        }

        "full data with international address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).success.value
            .set(DateOfBirthYesNoPage(index), true).success.value
            .set(DateOfBirthPage(index), dateOfBirth).success.value
            .set(CountryOfNationalityYesNoPage(index), true).success.value
            .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value
            .set(CountryOfNationalityPage(index), nonUkCountry).success.value
            .set(NationalInsuranceYesNoPage(index), false).success.value
            .set(CountryOfResidenceYesNoPage(index), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), nonUkCountry).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressUkYesNoPage(index), false).success.value
            .set(NonUkAddressPage(index), nonUkAddress).success.value
            .set(PassportDetailsYesNoPage(index), false).success.value
            .set(IDCardDetailsYesNoPage(index), false).success.value

          val helper = injector.instanceOf[OtherIndividualAnswersHelper]

          val result: Option[Seq[AnswerSection]] = helper.otherIndividuals(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.otherIndividual.subheading"),
              rows = Seq(
                AnswerRow(label = "otherIndividual.name.checkYourAnswersLabel", answer = Html(name.toString), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "otherIndividual.dateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.dateOfBirth.checkYourAnswersLabel", answer = Html("16 February 1960"), changeUrl = Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationality.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(rts.CountryOfNationalityController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.nationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.NationalInsuranceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidence.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.addressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.addressUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "site.address.international.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(rts.NonUkAddressController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.passportDetailsYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.PassportDetailsYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.idCardDetailsYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString)
              ),
              sectionKey = None,
              headingArgs = Seq(index + 1)
            )
          ))
        }

        "full data with UK address" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).success.value
            .set(DateOfBirthYesNoPage(index), true).success.value
            .set(DateOfBirthPage(index), dateOfBirth).success.value
            .set(CountryOfNationalityYesNoPage(index), true).success.value
            .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value
            .set(NationalInsuranceYesNoPage(index), false).success.value
            .set(CountryOfResidenceYesNoPage(index), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(AddressYesNoPage(index), true).success.value
            .set(AddressUkYesNoPage(index), true).success.value
            .set(UkAddressPage(index), ukAddress).success.value
            .set(PassportDetailsYesNoPage(index), false).success.value
            .set(IDCardDetailsYesNoPage(index), false).success.value

          val helper = injector.instanceOf[OtherIndividualAnswersHelper]

          val result: Option[Seq[AnswerSection]] = helper.otherIndividuals(userAnswers)

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some("answerPage.section.otherIndividual.subheading"),
              rows = Seq(
                AnswerRow(label = "otherIndividual.name.checkYourAnswersLabel", answer = Html(name.toString), changeUrl = Some(rts.NameController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "otherIndividual.dateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.dateOfBirth.checkYourAnswersLabel", answer = Html("16 February 1960"), changeUrl = Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.nationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.NationalInsuranceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.addressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.addressUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(rts.AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "site.address.uk.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(rts.UkAddressController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.passportDetailsYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.PassportDetailsYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString),
                AnswerRow(label = "otherIndividual.idCardDetailsYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(rts.IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId).url), canEdit = canEdit, labelArg = name.toString)
              ),
              sectionKey = None,
              headingArgs = Seq(index + 1)
            )
          ))
        }

    }
  }
}
