package com.jobosint.service.ai;

import com.jobosint.model.ai.InterviewProcess;
import com.jobosint.model.ai.TechnologyStack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class JobInterviewProcessServiceTest {
    @Autowired JobInterviewProcessService jobInterviewProcessService;

    @Test
    public void test() {
        Optional<InterviewProcess> interviewProcess = jobInterviewProcessService.parseJobDescription(
                """
                        Since 2018, Wisetack has been building transparent and intuitive consumer lending products that help service-based businesses thrive (think HVAC companies, veterinarians, or auto repair shops).
                                                
                        Our leadership team comes from top fintech companies such as Lending Club, Affirm, and Varo Money. And we're backed by leading VCs, including Insight Partners, Greylock, and Bain Capital Ventures (investors in Airbnb, LinkedIn, Instagram, Dropbox, and more).
                                                
                        Having raised $84M, we're a well-funded startup and have invested in people and technology while growing our partnerships --- responsibly.
                                                
                        We're also proud to have received recognition from the fintech world. Awards we've won include:
                                                
                        * [2023 Best Consumer Lending Program](https://www.wisetack.com/press/wisetack-wins-2023-best-consumer-lending-program-award) by Tearsheet
                        * [2023 Best Point of Sale Product](https://www.wisetack.com/press/fintech-breakthrough-names-wisetack-best-point-of-sale-solution) by Fintech Breakthrough
                        * [2022 Best Consumer Lending Solution](https://www.wisetack.com/press/wisetack-best-consumer-lending-solution-finovate) by Finovate
                        * [2022 Best Emerging Lending Platform](https://www.wisetack.com/press/wisetack-wins-best-emerging-lending-platform-by-lendit-fintech) by Lendit (now Fintech Nexus)
                                                
                        But what you'll find us gleaming about the most is the recognition from our own customers, particularly our sky-high NPS rating of 79. (For reference, the average score is 44 for[financial services](https://customergauge.com/benchmarks/blog/financial-services-nps-benchmarks) and 36 for[SaaS companies](https://customergauge.com/benchmarks/blog/nps-saas-net-promoter-score-benchmarks).)
                                                
                        As a result of our efforts in building this healthy company culture, we've been nominated to several[Great Places to Work](https://www.greatplacetowork.com/certified-company/7046639) lists, such as Best Small Workplaces, Best Workplaces in the Bay Area, and Best Workplaces in Financial Services \\& Insurance.
                                                
                        Like any startup, we're in it for the long haul, and we're looking for people willing to join our journey of building something special together.
                                                
                        This process starts with our company values, which guide us in everything we do --- and have played a critical role in our success. We valiantly abide by them, and would expect you to do the same:
                                                
                        * Put customers first (they're our raison d'être).
                        * Act fast (leverage our startup environment).
                        * Lead the way (show and tell).
                        * Take ownership (everyone is hands-on here).
                        * Be a good human (no egos, build financial products that do right by people).
                                                
                        Learn more about our values[here](https://www.wisetack.com/values).
                                                
                        ### Our Engineering Team Principles
                                                
                        ***Everyone is an engineer.*** No matter what their title is, everyone contributes to the code. Everyone delivers.
                                                
                        ***Initiative and ownership.*** We want every team member to lead the work they do. Design the best solutions and implement them. Ask for help from others when you need it.
                                                
                        ***One team.*** We are low ego and support each other.
                                                
                        ***Learning and growth.*** We believe growth is a huge motivator. We want everyone on the team to have an opportunity to learn and grow in their career. If you are smart and want to learn, you should be with us even if you don't have all the skills we need at the moment.
                                                
                        [Check out this post from our blog](https://www.wisetack.com/blog/wisetack-engineers) where we go into more detail about these principles.
                                                
                        ### The Role
                                                
                        Wisetack is looking for a Sr. Staff Software Engineer to be an early foundational member of our growing engineering department as a highly-impactful individual contributor (IC). You will be focused on technical concerns first and foremost; this is not a hybrid management/IC role. You will be a peer leader for your team, a force multiplier who sets examples of good code, best practices, and helps other ICs contribute to their full potential.
                                                
                        ### Responsibilities
                                                
                        * **Write good code.** You will implement new features, troubleshoot and fix bugs, and set an example for your teammates in how you do so.
                        * **Keep in mind a bigger picture than just your own work.** Maintain awareness of the work being conducted by the rest of your team, as well as other teams. Drive architectural discussions that ensure all the pieces fit together properly.
                        * **Take ownership.** You will keep an eye out for tech debt, buggy edge cases, and other pain points; bring them to the attention of your team, and help establish and prioritize a path to reducing or eliminating them.
                        * **Work efficiently and improve efficiency.** We select good tools, integrate them, and build automation to expedite our own work and get better quality at lower cost. You will need to think and work this way too.
                        * **Collaborate and communicate.** You will work closely with your hands-on Engineering Manager and your Product Manager, providing the deeply-technical perspective needed for projects to succeed.
                        * **And, most importantly, care about the business and move it forward.** We will win together. The company is very transparent, so you will be engaged and connected to our business goals.
                                                
                        ### Requirements
                                                
                        * Understanding of web applications and RESTful APIs; ideally from a full-stack perspective, but a backend-centric understanding is acceptable.
                        * Java, React, and/or AWS cloud-native services (DynamoDB, Lambda, SNS/SQS, etc) -- you don't need to be an expert in all 3 of these, but an ideal candidate will have strong experience with at least 2 (or with similar technologies)
                        * Learning and problem solving
                        * Communication, collaboration, analysis, and architecture
                        * Fintech experience is a plus
                                                
                        ### Interview Process
                                                
                        1. Application Review
                        2. Recruiter Screen
                        3. Engineering Mindset Interview
                        4. Technical Assessment - Java Part I
                        5. Virtual Onsite:
                           * Technical Assessment - Systems Design
                           * Technical Assessment - Java Part II
                           * Product Sense
                        6. Leadership Discussion - Meet with CTO
                        7. References
                        8. Offer
                                                
                        *The range of base salary for the position is between $170,800 - 239,100, plus equity, and [benefits](https://www.wisetack.com/careers). Please note that the base salary range is a guideline, and individual total compensation will vary based on factors such as qualifications, skill level and competencies.*
                                                
                        Spend a little time on our[About Us page](https://www.wisetack.com/about) and check out our[Press page](https://www.wisetack.com/press) and our[blog](https://www.wisetack.com/blog) for more. If you think this might be a fit, we'd love to hear from you!
                                                
                        """
        );
        System.out.println(interviewProcess);
    }

    @Test
    public void test2() {
        Optional<InterviewProcess> interviewProcess = jobInterviewProcessService.parseJobDescription(
                """
                        ****About The Team****
                                                
                        <br />
                                                
                        This role will be joining the Zoom Contact Center Software Engineering team. Zoom Contact Center is an omni-channel contact center that's optimized for video and integrated into the same Zoom experience. Zoom Contact Center brings unified communications together with contact center capabilities.
                                                
                        <br />
                                                
                        ****About the Role** :**
                                                
                        We are seeking a highly skilled and experienced Senior Software Engineer, Contact Center for our dynamic software development team. The ideal candidate will bring a proven track record of strong technical expertise in Java, cloud-based software development, and a passion for helping products that go to market.
                                                
                        <br />
                                                
                        **Responsibilities:**
                                                
                        * Collaborating with multidisciplinary teams to understand project requirements and objectives.
                                                
                        * Demonstrated technical proficiency, especially in Java development and cloud-based software architecture and Springboot.
                                                
                        * Overseeing the entire software development lifecycle, from concept to delivery, ensuring projects are completed on time and within scope.
                                                
                        * Driving continuous improvement initiatives, optimizing processes, and implementing best practices for software development.
                                                
                        * Ensuring software reliability and quality by participating in code reviews.
                                                
                        * Ensuring alignment with project goals, timelines, and quality standards.
                                                
                        <br />
                                                
                        <br />
                                                
                        **About You**
                                                
                        * Have a technical background with hands-on experience in Java development.
                                                
                        * Possess an advanced education in Computer Science or a related field or experience.
                                                
                        * Experience with cloud-based software development and architecture.
                                                
                        * Have architecture and design skills
                                                
                        * Have experience Contact Center or related products is a plus.
                                                
                        * Have excellent communication and interpersonal skills, making complex technical concepts accessible to diverse stakeholders.
                                                
                        <br />
                                                
                        **Salary Range or On Target Earnings:**
                                                
                        <br />
                                                
                        Minimum:
                        $133,500.00
                                                
                        <br />
                                                
                        Maximum:
                        $233,600.00
                                                
                        <br />
                                                
                        At Zoom, we offer a window of at least 5 days for you to apply because we believe in giving you every opportunity. Below is the potential closing date, just in case you want to mark it on your calendar. We look forward to receiving your application!
                                                
                        <br />
                                                
                        Anticipated Position Close Date:
                        04/15/24
                                                
                        <br />
                                                
                        In addition to the base salary and/or OTE listed Zoom has a Total Direct Compensation philosophy that takes into consideration; base salary, bonus and equity value.
                                                
                        Information about Zoom's benefits is on our careers page [++here++](https://explore.zoom.us/media/benefits_brochure.pdf#page=40).
                                                
                        <br />
                                                
                        Note: Starting pay will be based on a number of factors and commensurate with qualifications \\& experience.
                                                
                        <br />
                                                
                        We also have a location based compensation structure; there may be a different range for candidates in this and other locations.
                                                
                        <br />
                                                
                        **Ways of Working**  \s
                                                
                        Our structured hybrid approach is centered around our offices and remote work environments. The work style of each role, Hybrid, Remote, or In-Person is indicated in the job description/posting.
                                                
                        <br />
                                                
                        **Benefits**  \s
                                                
                        As part of our award-winning workplace culture and commitment to delivering happiness, our benefits program offers a variety of perks, benefits, and options to help employees maintain their physical, mental, emotional, and financial health; support work-life balance; and contribute to their community in meaningful ways. Click [Learn](https://careers.zoom.us/benefits)[](http://careers.zoom.us/benefits)for more information.
                                                
                        <br />
                                                
                        **About Us**  \s
                                                
                        Zoomies help people stay connected so they can get more done together. We set out to build the best collaboration platform for the enterprise, and today help people communicate better with products like Zoom Contact Center, Zoom Phone, Zoom Events, Zoom Apps, Zoom Rooms, and Zoom Webinars. \s
                                                
                        We're problem-solvers, working at a fast pace to design solutions with our customers and users in mind. Here, you'll work across teams to deliver impactful projects that are changing the way people communicate and enjoy opportunities to advance your career in a diverse, inclusive environment.
                                                
                        <br />
                                                
                        **Our Commitment​**  \s
                                                
                        We believe that the unique contributions of all Zoomies is the driver of our success. To make sure that our products and culture continue to incorporate everyone's perspectives and experience we never discriminate on the basis of race, religion, national origin, gender identity or expression, sexual orientation, age, or marital, veteran, or disability status. Zoom is proud to be an equal opportunity workplace and is an affirmative action employer. All your information will be kept confidential according to EEO guidelines.
                                                
                        <br />
                                                
                        We welcome people of different backgrounds, experiences, abilities and perspectives including qualified applicants with arrest and conviction records and any qualified applicants requiring reasonable accommodations in accordance with the law. If you need any assistance or accommodations due to a medical condition, or if you need assistance accessing our website or completing the application process, please let us know by emailing us at [careers@zoom.us](mailto:careers@zoom.us).
                        #LI-Remote
                                         
                        """
        );
        System.out.println(interviewProcess);
    }
}
