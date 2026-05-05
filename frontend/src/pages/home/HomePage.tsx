import Navbar from "./components/Navbar";
import HeroSection from "./components/HeroSection";
import OverviewSection from "./components/OverviewSection";
import FeaturesSection from "./components/FeaturesSection";
import SolutionsSection from "./components/SolutionsSection";
import HowItWorksSection from "./components/HowItWorksSection";
import FinalCtaSection from "./components/FinalCtaSection";
import "./HomePage.css";

export default function HomePage() {
  return (
    <>
      <Navbar />
      <main className="scroll">
        <HeroSection />
        <OverviewSection />
        <FeaturesSection />
        <SolutionsSection />
        <HowItWorksSection />
        <FinalCtaSection />
      </main>
    </>
  );
}
